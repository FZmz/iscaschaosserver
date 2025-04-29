package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.*;
import com.iscas.lndicatormonitor.dto.PressTestDTO;
import com.iscas.lndicatormonitor.dto.StartPlayDTO;
import com.iscas.lndicatormonitor.dto.presstest.PressTestResDTO;
import com.iscas.lndicatormonitor.utils.GoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;


@Service
public class AsyncService {
    private static final Logger log = LoggerFactory.getLogger(AsyncService.class);
    @Value("${chaos.mesh.url}")
    private String chaosMeshUrl;

    @Autowired
    private PlanService planService;

    @Autowired
    private PresscorrelationService presscorrelationService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    RecordService recordService;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    FaultcorrelationService faultcorrelationService;
    @Autowired
    ObservedIndexService observedIndexService;
    @Autowired
    ObservedcorrelationService observedcorrelationService;

    @Autowired
    WorkflowcorrelationService workflowcorrelationService;

    @Autowired
    PlandurationService plandurationService;
    @Autowired
    GoUtils goUtils;
    @Async
    public void performAsyncTasks(Plan plan, StartPlayDTO startPlayDTO) throws Exception {
        // 这里放入你需要异步执行的代码
        // 1.1 故障注入前进行压测 暂时默认5min
        String durationStr = plandurationService.selectDurationByPlanId(plan.getId());
        Integer duration = 0;
        if (durationStr != null && !durationStr.isEmpty()) {
             duration = Integer.parseInt(durationStr.replace("s", "")) * 1000;
            System.out.println("压测时间：" + duration);
            // 使用duration变量
        } else {
            // 处理未找到项的情况
             duration = 300000;
        }
        PressTestDTO pressTestDTO = new PressTestDTO();
        pressTestDTO.setContent(plan.getPressContent());
        Integer pressIdStart = toPressTest(pressTestDTO);

        System.out.println("我开始啦！"+ pressIdStart);
        // 应该等待多长时间呢？ 暂时默认5min
        Thread.sleep(duration);
        // 1.2 故障注入中进行压测
        Integer recordId  = (Integer) goUtils.toInject(startPlayDTO);
        // 应该等待多长时间呢？ 暂时默认5min
        Thread.sleep(duration);
        Record record = recordService.selectByPrimaryKey(recordId);
        record.setPressStatus(3);
        recordService.updateByPrimaryKey(record);
        // 1.3 故障注入后进行压测
        Integer pressIdEnd =  toPressTest(pressTestDTO);
        Presscorrelation presscorrelation1 = new Presscorrelation();
        Presscorrelation presscorrelation2 = new Presscorrelation();
        presscorrelation1.setRecordId(recordId);
        presscorrelation2.setRecordId(recordId);
        presscorrelation1.setK6Id(pressIdStart);
        presscorrelation2.setK6Id(pressIdEnd);
        presscorrelationService.insert(presscorrelation1);
        presscorrelationService.insert(presscorrelation2);
        record =  recordService.selectByPrimaryKey(recordId);
        record.setEndTime(new Date());
        recordService.updateByPrimaryKey(record);
    }

    public Integer toPressTest(PressTestDTO pressTestDTO){
        String request_preT_url = chaosMeshUrl + "/api/real_time/k6/";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<PressTestDTO> entity = new HttpEntity<>(pressTestDTO, headers);
        ResponseEntity<PressTestResDTO> response = restTemplate.exchange(request_preT_url, HttpMethod.POST, entity, PressTestResDTO.class);
        return response.getBody().getId();
    }

}
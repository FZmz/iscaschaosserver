package com.iscas.lndicatormonitor.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.*;
import com.iscas.lndicatormonitor.dto.PressTestDTO;
import com.iscas.lndicatormonitor.dto.StartPlayDTO;
import com.iscas.lndicatormonitor.dto.SubWorkflowDTO;
import com.iscas.lndicatormonitor.dto.presstest.PressTestResDTO;
import com.iscas.lndicatormonitor.service.*;
import com.iscas.lndicatormonitor.service.impl.PrometheusIndexNewServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class GoUtils {
    @Autowired
    PlanService planService;
    @Autowired
    RecordService recordService;
    @Autowired
    FaultcorrelationService faultcorrelationService;
    @Autowired
    ObservedIndexService observedIndexService;
    @Autowired
    ObservedcorrelationService observedcorrelationService;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    WorkflowcorrelationService workflowcorrelationService;

    @Autowired
    PresscorrelationService presscorrelationService;
    @Autowired
    PlandurationService plandurationService;
    @Autowired
    private RestTemplate restTemplate;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final Logger logger = LoggerFactory.getLogger(PrometheusIndexNewServiceImpl.class);

    @Value("${chaos.mesh.url}")
    private String chaosMeshUrl;
    public Object toInject(StartPlayDTO startPlayDTO) throws Exception {

        String workflowName = "workflow-" + UUID.randomUUID().toString().substring(0, 8);
        System.out.println("我开始注入啦！");
        final String url = chaosMeshUrl + "/api/real_time/workflow";
        ObjectMapper objectMapper = new ObjectMapper();
        // 根据planid 查询基本信息 准备新增record
        int planId = startPlayDTO.getPlayId();
        Plan plan = planService.getPlanById(planId);
        Record record = new Record();
        record.setPlanId(planId);
        record.setPlayerId(startPlayDTO.getPlayerId());
        record.setRecordProgress(0);
        record.setPressStatus(0);
        record.setStartTime(new Date());
        // 关联对应指标至此记录
        /*
         * 1、根据planId 查询所有故障配置 从faultcorrelation表查询
         * 2、根据故障配置查询所有指标
         * 3、一一跟record关联 存放进数据库
         * */
        // 新增record
        recordService.insert(record);

        List<Faultcorrelation> faultcorrelationList = faultcorrelationService.selectByPlanId(planId);
        for (Faultcorrelation faultcorrelation : faultcorrelationList) {
            int faultConfigId = faultcorrelation.getFaultConfigId();
            List<ObservedIndex> observedIndexList = observedIndexService.selectByFaultConfigId(faultConfigId);
            // 确定这里不是冗余吗 record本来就与plan有关系了，所以可以通过plan找到相应的faultconfig，然后通过faultconfig找到observedIndex
            // 唯一的好处就是到时查询record记录详情信息的时候可以省去一些操作
            for (ObservedIndex observedIndex : observedIndexList) {
                // 获取到每个故障下对应的指标 新增correlation 插入数据库
                Observedcorrelation observedcorrelation = new Observedcorrelation();
                observedcorrelation.setRecordId(record.getId());
                observedcorrelation.setObservedId(observedIndex.getId());
                observedcorrelationService.insert(observedcorrelation);
            }
        }
        // 请求go端接口 取出workflow 发送请求
        SubWorkflowDTO subWorkflowDTO = new SubWorkflowDTO();
        int workflowId = planService.getPlanById(record.getPlanId()).getWorkflowId();
        String workflowTemplate = workflowService.getWorkflowById(planService.getPlanById(record.getPlanId()).getWorkflowId()).getContent();
        // 替换模板当中的workflowname
        workflowTemplate =  workflowTemplate.replace("workflowname",workflowName);
        // 添加workflow与record的联系
        Workflowcorrelation workflowcorrelation = new Workflowcorrelation();
        workflowcorrelation.setName(workflowName);
        workflowcorrelation.setRecordId(record.getId());
        workflowcorrelation.setWorkflowId(workflowId);
        workflowcorrelationService.save(workflowcorrelation);

        Object workflowContent = objectMapper.readValue(workflowTemplate, Object.class);
        System.out.println(workflowContent);
        // 设置workflow
        subWorkflowDTO.setWorkflow(workflowContent);
        // 设置pressTest
        if (!plan.getPressContent().isEmpty()){
            PressTestDTO pressTestDTO = new PressTestDTO();
            pressTestDTO.setContent(plan.getPressContent());
            // 如果设置了压测
            subWorkflowDTO.setK6(pressTestDTO);
        }
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SubWorkflowDTO> entity = new HttpEntity<>(subWorkflowDTO, headers);
        if (!plan.getPressContent().isEmpty()){
            ResponseEntity<PressTestResDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, PressTestResDTO.class);
            Presscorrelation presscorrelation = new Presscorrelation();
            presscorrelation.setK6Id(response.getBody().getId());
            presscorrelation.setRecordId(record.getId());
            presscorrelationService.insert(presscorrelation);
            System.out.println("我进行了压测哦！" + response.getBody().getId());
        }else {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
        }

        // 更新record状态为1 即进行中
        record.setRecordStatus(1);
        record.setPressStatus(2);
        if (plan.getPressContent().isEmpty()) {
            // FIXME 这里不能这么判断 应该是根据实际workflow的结束时间进行判断
            String durationStr = plandurationService.selectDurationByPlanId(plan.getId());
            int duration = Integer.parseInt(durationStr.replace("s", ""));
            // 在单独的线程中执行暂停操作
            executorService.submit(() -> {
                try {
                    // 停滞duration秒
                    TimeUnit.SECONDS.sleep(duration);
                    // 更新record状态为1 即进行中
                    record.setRecordStatus(2);
                    record.setPressStatus(0);
                    record.setEndTime(new Date());
                    System.out.println("更新了");
                    recordService.updateByPrimaryKeySelective(record);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            });
        }
        recordService.updateByPrimaryKeySelective(record);
        return record.getId();
    }


    public String findSvcbyPodLabel(String podLabel,String nameSpace) throws Exception {
        String url = chaosMeshUrl + "/api/real_time/service/"+nameSpace+"?" + podLabel;
        System.out.println("url: " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
        String str = response.getBody().toString();
        System.out.println("匹配结果1：" + str);
        String str1 = str.substring(1,str.length()-1);
        String str2[] = str1.split(",");
        for (String s : str2){
            if (s.contains(podLabel)){
                System.out.println("匹配结果2：" + s);
                return s;
            }
        }
        // 返回包含podLabel的service
        return str2[0];
    }
}

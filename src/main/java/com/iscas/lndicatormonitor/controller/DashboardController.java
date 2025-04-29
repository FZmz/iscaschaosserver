package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.service.PlanService;
import com.iscas.lndicatormonitor.service.RecordService;
import com.iscas.lndicatormonitor.service.UsersService;
import com.iscas.lndicatormonitor.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Value("${chaos.mesh.token}")
    private String chaosMeshToken;

    @Autowired
    PlanService planService;

    @Autowired
    RecordService recordService;

    @Autowired
    UsersService usersService;

    @Autowired
    WorkflowService workflowService;


    @GetMapping("/getPlanSumNum")
    @OperationLog("获取实验总数")
    public CustomResult getPlanSumNum() throws Exception {
        int planNum = planService.getPlanSumNum();
        int planNum1 = recordService.getStatus1();
        int planNum2 = recordService.getStatus2();
        int planNum3 = recordService.getStatus3();

        int[] array = new int[4];

        //计划总数
        array[0] = planNum;
        //状态=1未开始
        array[1] = planNum-planNum1-planNum2-planNum3;
        //状态=2进行中
        array[2] = planNum1;
        //状态=3已结束
        array[3] = planNum2+planNum3;

        return CustomResult.ok(array);
    }

    @GetMapping("/getNameNum")
    @OperationLog("获取用户实验次数")
    public CustomResult getNameNum() throws Exception {
        List<Users> list = usersService.getAllUsers();
        Map<String, Integer> map = new HashMap<>();
        for ( Users users : list
             ) {
            map.put(users.getRealName(),recordService.getNumOfName(users.getId()));
        }

        return CustomResult.ok(map);
    }

    @GetMapping("/getSpaceNum")
    @OperationLog("获取实验总体异常统计数据")
    public CustomResult getSpaceNum() throws Exception {
//        String url = "http://39.104.62.233:30067/api/real_time/workflow";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", this.token);
//        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
//
//        // 假设response.getBody()可以直接返回所需的数据结构
//        List<Map<String, Object>> responseData = (List<Map<String, Object>>) response.getBody();
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("sock-shop",69);
        countMap.put("chaos-mesh",5);
        return CustomResult.ok(countMap);
    }

    @GetMapping("/getTimeNum")
    @OperationLog("获取实验总体异常统计数据")
    public CustomResult getTimeNum() throws Exception {

        List<Map<String, Object>> objectMap = recordService.getNumOfTime();
        return CustomResult.ok(objectMap);
    }

    @GetMapping("/getTimeNum2")
    @OperationLog("获取实验总体异常统计数据")
    public CustomResult getTimeNum2() throws Exception {


        List<Map<String, Object>> objectMap = recordService.getCount();

        int sum = 0;
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < objectMap.size(); i++) {
            int dataCount = ((Number) objectMap.get(i).get("dataCount")).intValue();

            if (i >= 4) {
                sum += dataCount;
            } else {
                result.add(dataCount);
            }
        }
        while (result.size()<3){
            result.add(0);
        }

        result.add(sum);

        return CustomResult.ok(result);
    }

}

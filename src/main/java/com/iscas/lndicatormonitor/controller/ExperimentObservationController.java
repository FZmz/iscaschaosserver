package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/experimentObservation")
public class ExperimentObservationController {

    /**
     * 1. 获取异常聚合信息
     * 返回实验总体异常统计数据
     */
    @GetMapping("/getAggregateInfo")
    @OperationLog("获取实验总体异常统计数据")
    public CustomResult getAggregateInfo(@RequestParam String planId) {
        // 模拟实验统计信息
        String duration = "5分钟";
        int affectedServices = 3;
        int totalRequests = 1000;
        int successfulRequests = 850;
        int failedRequests = 150;
        int sloViolations = 5;
        double successRate = (double) successfulRequests / totalRequests * 100;

        String description = String.format("此次实验共持续%s，其中共%d个服务受到了影响，" +
                "实验期间总请求数为 %d，其中成功 %d，失败 %d。实验过程中发现 %d 次 SLO 违规，" +
                "总体成功率为 %.2f%%。", duration, affectedServices, totalRequests, successfulRequests, failedRequests, sloViolations, successRate);

        // 返回详细的实验信息
        Map<String, Object> result = new HashMap<>();
        result.put("planId", planId);
        result.put("duration", duration);
        result.put("affectedServices", affectedServices);
        result.put("totalRequests", totalRequests);
        result.put("successfulRequests", successfulRequests);
        result.put("failedRequests", failedRequests);
        result.put("sloViolations", sloViolations);
        result.put("successRate", successRate);
        result.put("description", description);

        return new CustomResult(200, "Success", result);
    }

    /**
     * 2. 获取异常服务列表
     * 返回异常服务及对应的异常原因
     */
    @GetMapping("/getAbnormalServiceList")
    @OperationLog("获取异常服务列表")
    public CustomResult getAbnormalServiceList(@RequestParam String planId) {
        // 使用真实服务名，并将异常信息用英文表示
        Map<String, List<String>> abnormalServices = new HashMap<>();
        abnormalServices.put("OrderService", Arrays.asList("SLO violation", "High CPU usage", "High memory usage"));
        abnormalServices.put("PaymentService", Arrays.asList("High network latency", "High packet loss"));
        abnormalServices.put("InventoryService", Arrays.asList("Disk IO latency", "Service unavailable"));

        Map<String, Object> result = new HashMap<>();
        result.put("planId", planId);
        result.put("abnormalServices", abnormalServices);

        return new CustomResult(200, "Success", result);
    }

    /**
     * 3. 获取异常链路聚合信息
     * 返回异常链路统计和细节信息
     */
    @GetMapping("/getAbnormalLinkInfo")
    @OperationLog("获取异常链路聚合信息")
    public CustomResult getAbnormalLinkInfo(@RequestParam String planId) {
        Map<String, Object> abnormalLinkInfo = new HashMap<>();
        abnormalLinkInfo.put("totalAbnormalLinks", 4);
        abnormalLinkInfo.put("details", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("link", "OrderService -> PaymentService");
                    put("issue", "Increased latency");
                }},
                new HashMap<String, Object>() {{
                    put("link", "PaymentService -> InventoryService");
                    put("issue", "Increased request failure rate");
                }},
                new HashMap<String, Object>() {{
                    put("link", "OrderService -> UserService");
                    put("issue", "High response time");
                }},
                new HashMap<String, Object>() {{
                    put("link", "InventoryService -> ShippingService");
                    put("issue", "Packet loss detected");
                }}
        ));

        Map<String, Object> result = new HashMap<>();
        result.put("planId", planId);
        result.put("abnormalLinkInfo", abnormalLinkInfo);

        return new CustomResult(200, "Success", result);
    }

    /**
     * 4. 获取异常日志聚合信息
     * 返回实验中产生的异常日志的聚合结果
     */
    @GetMapping("/getAbnormalLogInfo")
    @OperationLog("获取异常日志聚合信息")
    public CustomResult getAbnormalLogInfo(@RequestParam String planId) {
        Map<String, Object> abnormalLogInfo = new HashMap<>();
        abnormalLogInfo.put("totalLogs", 100);
        abnormalLogInfo.put("logDetails", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("service", "OrderService");
                    put("log", "OOM Error detected");
                }},
                new HashMap<String, Object>() {{
                    put("service", "PaymentService");
                    put("log", "Connection timeout");
                }},
                new HashMap<String, Object>() {{
                    put("service", "InventoryService");
                    put("log", "Disk IO error");
                }}
        ));

        Map<String, Object> result = new HashMap<>();
        result.put("planId", planId);
        result.put("abnormalLogInfo", abnormalLogInfo);

        return new CustomResult(200, "Success", result);
    }
}
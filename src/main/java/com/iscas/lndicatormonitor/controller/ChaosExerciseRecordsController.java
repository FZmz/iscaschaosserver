package com.iscas.lndicatormonitor.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Planv2;
import com.iscas.lndicatormonitor.domain.recordv2.ChaosExerciseRecords;
import com.iscas.lndicatormonitor.dto.recordv2.ChaosExeRecordsDTO;
import com.iscas.lndicatormonitor.dto.recordv2.RecordListDTO;
import com.iscas.lndicatormonitor.dto.recordv2.Recordv2QueryCriteria;
import com.iscas.lndicatormonitor.dto.recordv2.WorkflowSummaryDTO;
import com.iscas.lndicatormonitor.service.ApplicationService;
import com.iscas.lndicatormonitor.service.ChaosExerciseRecordsService;
import com.iscas.lndicatormonitor.service.ConfigTargetService;
import com.iscas.lndicatormonitor.service.FaultcorrelationService;
import com.iscas.lndicatormonitor.service.LoadTaskService;
import com.iscas.lndicatormonitor.service.Planv2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/exerciseRecords")
@Slf4j
public class ChaosExerciseRecordsController {
    @Value("${coroot.url}")
    private String corootUrl;

    @Value("${coroot.projectId}")
    private String projectId;

    @Value("${coroot.cookie}")
    private String cookie;
    @Autowired
    private ChaosExerciseRecordsService chaosExerciseRecordsService;

    @Autowired
    ConfigTargetService configTargetService;

    @Autowired
    FaultcorrelationService faultcorrelationService;

    @Autowired
    private Planv2Service planv2Service;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private LoadTaskService loadTaskService;

    @PostMapping
    @OperationLog("添加演练记录")
    public CustomResult addExerciseRecord(@RequestBody ChaosExerciseRecords record) {
        boolean success = chaosExerciseRecordsService.save(record);
        if(success) {
            return CustomResult.ok();
        } else {
            return CustomResult.fail("新增演练记录失败");
        }
    }
    @GetMapping("/basicInfo")
    @OperationLog("获取演练记录基本信息")
    public CustomResult getBasicInfo(@RequestParam Integer recordId) {
        try {
            ChaosExeRecordsDTO dto = chaosExerciseRecordsService.getBasicInfo(recordId);
            return CustomResult.ok(dto);
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }
    @GetMapping("/getWorkflowInfoByRecordId")
    @OperationLog("获取工作流信息")
    public CustomResult getWorkflowInfoByRecordId(@RequestParam Integer recordId) {
        try {
            WorkflowSummaryDTO summary = chaosExerciseRecordsService.getWorkflowInfoByRecordId(recordId);
            return CustomResult.ok(summary);
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }

    @GetMapping("/getWorkflowLog")
    @OperationLog("获取工作流日志")
    public CustomResult getWorkflowLog(@RequestParam String recordId) {
        // 创建示例日志内容（实际应该从存储或日志系统中获取）
        List<String> logs = new ArrayList<>();
        // 添加日志内容
        logs.add("2025-01-21T21:47:37.754Z\tINFO\tworkflow-fault-config-reconciler\tcontrollers/fault_config_node_reconciler.go:80\t检查时间 决定是否执行调谐 resolve fault config node\t{\"node\": \"sock-shop/fault-86b8c06c-pnnw6\", \"step\": 3, \"now\": \"2025-01-20T21:47:37.727Z\", \"next time\": \"2025-01-15 02:08:37 +0000 UTC\"}");
        logs.add("2025-01-21T21:47:37.754Z\tINFO\tworkflow-fault-config-reconciler\tcontrollers/fault_config_node_reconciler.go:92\towner reference\t{\"APIVersion\": \"\", \"Kind\": \"\", \"Name\": \"fault-86b8c06c-pnnw6\", \"UID\": \"98528a3b-26d1-472e-b42b-f585ed7c5e0b\"}");
        logs.add("2025-01-21T21:47:37.853Z\tINFO\tworkflow-fault-config-reconciler\tcontrollers/fault_config_node_reconciler.go:66\towner reference\t{\"APIVersion\": \"\", \"Kind\": \"\", \"Name\": \"fault-7acfbc81-s5g84\", \"UID\": \"b48a5e68-4b5e-426b-8d16-8d6d59e6f6c6\"}");

        return CustomResult.ok(logs);
    }

    @GetMapping("/getEventSummarize")
    @OperationLog("获取事件摘要")
    public CustomResult getEventSummarize(@RequestParam String recordId) {
        log.info("开始获取事件摘要, recordId={}", recordId);
        
        ChaosExerciseRecords chaosExerciseRecords = chaosExerciseRecordsService.getById(recordId);
        if (chaosExerciseRecords == null) {
            log.warn("记录不存在, recordId={}", recordId);
            return CustomResult.fail("记录不存在");
        }

        // 获取记录状态
        Integer status = chaosExerciseRecords.getRecordStatus();
        log.info("记录状态: status={}", status);
        
        // 获取计划信息以检查是否有压测
        Planv2 plan = planv2Service.getById(chaosExerciseRecords.getPlanId());
        boolean hasLoad = plan != null && plan.getLoadId() != null && !plan.getLoadId().isEmpty();
        log.info("是否包含压测: hasLoad={}", hasLoad);

        if (hasLoad) {
            // 有压测模式的状态处理
            switch (status) {
                case 0: // 未运行
                    log.info("有压测-未运行状态");
                    return CustomResult.fail("演练尚未开始，请等待");
                    
                case 1: // 初始压测
                    log.info("有压测-初始压测状态");
                    return CustomResult.fail("正在进行初始压测，请等待故障注入");
                    
                case 2: // 故障注入
                case 3: // 收尾压测
                    log.info("有压测-故障注入或收尾压测状态");
                    try {
                        Date start = chaosExerciseRecords.getStartTime();
                        Date end = new Date(); // 使用当前时间作为结束时间
                        
                        if (start == null) {
                            log.error("记录开始时间为空");
                            return CustomResult.fail("记录开始时间异常");
                        }
                        
                        return getEventSummaryByTimeRange(chaosExerciseRecords, start, end);
                    } catch (Exception e) {
                        log.error("获取事件摘要失败", e);
                        return CustomResult.fail("获取异常事件摘要失败: " + e.getMessage());
                    }
                    
                case 4: // 执行成功
                    log.info("有压测-执行成功状态");
                    try {
                        Date start = chaosExerciseRecords.getStartTime();
                        Date end = chaosExerciseRecords.getEndTime();
                        
                        if (start == null || end == null) {
                            log.error("记录时间异常: startTime={}, endTime={}", start, end);
                            return CustomResult.fail("记录时间异常");
                        }
                        
                        return getEventSummaryByTimeRange(chaosExerciseRecords, start, end);
                    } catch (Exception e) {
                        log.error("获取事件摘要失败", e);
                        return CustomResult.fail("获取异常事件摘要失败: " + e.getMessage());
                    }
                    
                case 5: // 执行失败
                    log.info("有压测-执行失败状态");
                    if (chaosExerciseRecords.getStartTime() == null || chaosExerciseRecords.getEndTime() == null) {
                        log.error("执行失败且时间异常: startTime={}, endTime={}", 
                            chaosExerciseRecords.getStartTime(), chaosExerciseRecords.getEndTime());
                        return CustomResult.fail("演练执行失败，请检查日志并重新执行");
                    }
                    try {
                        return getEventSummaryByTimeRange(chaosExerciseRecords, 
                                chaosExerciseRecords.getStartTime(), 
                                chaosExerciseRecords.getEndTime());
                    } catch (Exception e) {
                        log.error("获取事件摘要失败", e);
                        return CustomResult.fail("获取异常事件摘要失败: " + e.getMessage());
                    }
                    
                default:
                    log.warn("未知的记录状态: {}", status);
                    return CustomResult.fail("未知的记录状态");
            }
        } else {
            // 无压测模式的状态处理
            switch (status) {
                case 0: // 未运行
                    log.info("无压测-未运行状态");
                    return CustomResult.fail("演练尚未开始，请等待");
                    
                case 1: // 故障注入中
                    log.info("无压测-故障注入状态");
                    try {
                        Date start = chaosExerciseRecords.getStartTime();
                        Date end = new Date(); // 使用当前时间作为结束时间
                        
                        if (start == null) {
                            log.error("记录开始时间为空");
                            return CustomResult.fail("记录开始时间异常");
                        }
                        
                        return getEventSummaryByTimeRange(chaosExerciseRecords, start, end);
                    } catch (Exception e) {
                        log.error("获取事件摘要失败", e);
                        return CustomResult.fail("获取异常事件摘要失败: " + e.getMessage());
                    }
                    
                case 2: // 执行成功
                    log.info("无压测-执行成功状态");
                    try {
                        Date start = chaosExerciseRecords.getStartTime();
                        Date end = chaosExerciseRecords.getEndTime();
                        
                        if (start == null || end == null) {
                            log.error("记录时间异常: startTime={}, endTime={}", start, end);
                            return CustomResult.fail("记录时间异常");
                        }
                        
                        return getEventSummaryByTimeRange(chaosExerciseRecords, start, end);
                    } catch (Exception e) {
                        log.error("获取事件摘要失败", e);
                        return CustomResult.fail("获取异常事件摘要失败: " + e.getMessage());
                    }
                    
                case 3: // 执行失败
                    log.info("无压测-执行失败状态");
                    if (chaosExerciseRecords.getStartTime() == null || chaosExerciseRecords.getEndTime() == null) {
                        log.error("执行失败且时间异常: startTime={}, endTime={}", 
                            chaosExerciseRecords.getStartTime(), chaosExerciseRecords.getEndTime());
                        return CustomResult.fail("演练执行失败，请检查日志并重新执行");
                    }
                    try {
                        return getEventSummaryByTimeRange(chaosExerciseRecords, 
                                chaosExerciseRecords.getStartTime(), 
                                chaosExerciseRecords.getEndTime());
                    } catch (Exception e) {
                        log.error("获取事件摘要失败", e);
                        return CustomResult.fail("获取异常事件摘要失败: " + e.getMessage());
                    }
                    
                default:
                    log.warn("未知的记录状态: {}", status);
                    return CustomResult.fail("未知的记录状态");
            }
        }
    }

    private CustomResult getEventSummaryByTimeRange(ChaosExerciseRecords chaosExerciseRecords, Date start, Date end) {
        try {
            long startTime = start.getTime();
            long endTime = end.getTime();
            // 构建完整的 URL
            String url = String.format("%s%s/overview/health?query=&from=%d&to=%d",
                    corootUrl, projectId, startTime, endTime);

            // 创建 RestTemplate 实例
            RestTemplate restTemplate = new RestTemplate();
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);
            headers.set("Accept", "*/*");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<JSONObject> response = restTemplate.exchange(url, HttpMethod.GET, entity, JSONObject.class);
            System.out.println(url);
            JSONObject jsonObject = response.getBody();
            if (jsonObject == null) {
                throw new RuntimeException("获取响应数据为空");
            }

            JSONObject data = jsonObject.getJSONObject("data");
            if (data == null) {
                throw new RuntimeException("响应数据中缺少 data 字段");
            }

            // 获取 "health" 数组
            JSONArray healthArray = data.getJSONArray("health");
            if (healthArray == null) {
                throw new RuntimeException("响应数据中缺少 health 数组");
            }

            // 用来存放符合条件的整个 JSON 对象
            List<JSONObject> filteredHealthObjects = new ArrayList<>();

            // 遍历 "health" 数组
            for (int i = 0; i < healthArray.size(); i++) {
                JSONObject healthObj = healthArray.getJSONObject(i);
                String id = healthObj.getString("id");
                JSONObject namespaceJson = JSON.parseObject(configTargetService.selectByFaultConfigId(
                        faultcorrelationService.selectByPlanId(chaosExerciseRecords.getPlanId()).get(0).getFaultConfigId()
                ).getContent());
                if (id != null && id.startsWith(namespaceJson.getString("namespace"))) {
                    filteredHealthObjects.add(healthObj);
                }

            }

            // 创建各种服务问题列表
            List<String> servicesWithHighErrorRate = new ArrayList<>();
            List<String> servicesWithHighLatency = new ArrayList<>();
            List<String> servicesWithUpstreamErrors = new ArrayList<>();
            List<String> servicesWithInstanceIssues = new ArrayList<>();
            List<String> servicesWithRestarts = new ArrayList<>();
            List<String> servicesWithHighCPU = new ArrayList<>();
            List<String> servicesWithHighMemory = new ArrayList<>();
            List<String> servicesWithHighIoLoad = new ArrayList<>();
            List<String> servicesWithHighDiskUsage = new ArrayList<>();
            List<String> servicesWithNetworkIssues = new ArrayList<>();
            List<String> servicesWithDnsIssues = new ArrayList<>();
            List<String> servicesWithLogIssues = new ArrayList<>();

            // 分析每个服务的健康状况
            for (JSONObject healthObject : filteredHealthObjects) {
                String serviceId = healthObject.getString("id");
                analyzeServiceHealth(healthObject, serviceId, 
                        servicesWithHighErrorRate, servicesWithHighLatency, 
                        servicesWithUpstreamErrors, servicesWithInstanceIssues,
                        servicesWithRestarts, servicesWithHighCPU,
                        servicesWithHighMemory, servicesWithHighIoLoad,
                        servicesWithHighDiskUsage, servicesWithNetworkIssues,
                        servicesWithDnsIssues, servicesWithLogIssues);
            }

            // 创建度量指标列表
            List<Map<String, Object>> metrics = new ArrayList<>();
            metrics.add(createMetric("Errors", servicesWithHighErrorRate.size(), "错误率超过1%的服务", servicesWithHighErrorRate));
            metrics.add(createMetric("Latency", servicesWithHighLatency.size(), "延迟超过200ms的服务", servicesWithHighLatency));
            metrics.add(createMetric("Upstreams", servicesWithUpstreamErrors.size(), "上游服务异常的服务", servicesWithUpstreamErrors));
            metrics.add(createMetric("Instances", servicesWithInstanceIssues.size(), "实例数异常的服务", servicesWithInstanceIssues));
            metrics.add(createMetric("Restarts", servicesWithRestarts.size(), "发生重启的服务", servicesWithRestarts));
            metrics.add(createMetric("CPU", servicesWithHighCPU.size(), "CPU使用率异常的服务", servicesWithHighCPU));
            metrics.add(createMetric("Memory", servicesWithHighMemory.size(), "内存使用率异常的服务", servicesWithHighMemory));
            metrics.add(createMetric("I/O Load", servicesWithHighIoLoad.size(), "I/O负载异常的服务", servicesWithHighIoLoad));
            metrics.add(createMetric("Disk", servicesWithHighDiskUsage.size(), "磁盘使用率异常的服务", servicesWithHighDiskUsage));
            metrics.add(createMetric("Network", servicesWithNetworkIssues.size(), "网络异常的服务", servicesWithNetworkIssues));
            metrics.add(createMetric("DNS", servicesWithDnsIssues.size(), "DNS解析异常的服务", servicesWithDnsIssues));
            metrics.add(createMetric("Logs", servicesWithLogIssues.size(), "出现异常日志的服务", servicesWithLogIssues));

            return new CustomResult(20000, "Success", metrics);
        } catch (Exception e) {
            throw new RuntimeException("获取事件摘要失败: " + null);
        }
    }

    private void analyzeServiceHealth(JSONObject healthObject, String serviceId,
                                    List<String> servicesWithHighErrorRate,
                                    List<String> servicesWithHighLatency,
                                    List<String> servicesWithUpstreamErrors,
                                    List<String> servicesWithInstanceIssues,
                                    List<String> servicesWithRestarts,
                                    List<String> servicesWithHighCPU,
                                    List<String> servicesWithHighMemory,
                                    List<String> servicesWithHighIoLoad,
                                    List<String> servicesWithHighDiskUsage,
                                    List<String> servicesWithNetworkIssues,
                                    List<String> servicesWithDnsIssues,
                                    List<String> servicesWithLogIssues) {
        
        String[] idParts = serviceId.split(":");
        String serviceName = idParts[idParts.length - 1];

        // 检查错误率
        JSONObject errors = healthObject.getJSONObject("errors");
        String errorValue = errors.getString("value").trim();
        if (!"".equals(errorValue)) {
            // 处理 "<1" 这样的特殊情况
            if (errorValue.startsWith("<")) {
                // 对于 "<1" 这样的值，取1作为阈值
                servicesWithHighErrorRate.add(serviceName);
            } else {
                try {
                    // 移除百分号并解析数值
                    double errorRate = Double.parseDouble(errorValue.replace("%", ""));
                    if (errorRate > 1.0) {
                        servicesWithHighErrorRate.add(serviceName);
                    }
                } catch (NumberFormatException e) {
                    log.warn("无法解析错误率值: {}", errorValue);
                }
            }
        }

        // 检查延迟
        JSONObject latency = healthObject.getJSONObject("latency");
        String latencyValue = latency.getString("value").trim();
        if (!"".equals(latencyValue)) {
            try {
                double latencyMs;
                if (latencyValue.endsWith("ms")) {
                    // 已经是毫秒单位，直接移除"ms"并解析
                    latencyMs = Double.parseDouble(latencyValue.replace("ms", ""));
                } else if (latencyValue.endsWith("s")) {
                    // 秒单位，移除"s"并解析，然后转换为毫秒
                    latencyMs = Double.parseDouble(latencyValue.replace("s", "")) * 1000;
                } else {
                    // 假设没有单位的话默认为毫秒
                    latencyMs = Double.parseDouble(latencyValue);
                }

                if (latencyMs > 200) {
                    servicesWithHighLatency.add(serviceName);
                }
            } catch (NumberFormatException e) {
                log.warn("无法解析延迟值: {}", latencyValue);
            }
        }

        // 检查上游服务
        JSONObject upstreams = healthObject.getJSONObject("upstreams");
        if (!"ok".equals(upstreams.getString("status")) && 
            !"".equals(upstreams.getString("value").trim())) {
            servicesWithUpstreamErrors.add(serviceName);
        }

        // 检查实例数
        JSONObject instances = healthObject.getJSONObject("instances");
        String instanceValue = instances.getString("value").trim();
        if (!"".equals(instanceValue)) {
            if (instanceValue.contains("/")) {
                String[] parts = instanceValue.split("/");
                try {
                    int current = Integer.parseInt(parts[0]);
                    int total = Integer.parseInt(parts[1]);
                    if (current != total) {
                        servicesWithInstanceIssues.add(serviceName);
                    }
                } catch (NumberFormatException e) {
                    log.warn("无法解析实例数值: {}", instanceValue);
                }
            } else {
                servicesWithInstanceIssues.add(serviceName);
            }
        }

        // 检查重启
        if (!"".equals(healthObject.getJSONObject("restarts").getString("value").trim())) {
            servicesWithRestarts.add(serviceName);
        }

        // 检查 CPU
        if (!"".equals(healthObject.getJSONObject("cpu").getString("value").trim())) {
            servicesWithHighCPU.add(serviceName);
        }

        // 检查内存
        if (!"".equals(healthObject.getJSONObject("memory").getString("value").trim())) {
            servicesWithHighMemory.add(serviceName);
        }

        // 检查 I/O 负载
        if (!"".equals(healthObject.getJSONObject("disk_io_load").getString("value").trim())) {
            servicesWithHighIoLoad.add(serviceName);
        }

        // 检查磁盘使用率
        if (!"".equals(healthObject.getJSONObject("disk_usage").getString("value").trim())) {
            servicesWithHighDiskUsage.add(serviceName);
        }

        // 检查网络
        JSONObject network = healthObject.getJSONObject("network");
        if (!"ok".equals(network.getString("value")) && 
            "warning".equalsIgnoreCase(network.getString("status").trim())) {
            servicesWithNetworkIssues.add(serviceName);
        }

        // 检查 DNS
        JSONObject dns = healthObject.getJSONObject("dns");
        if (!"".equals(dns.getString("value")) && 
            "warning".equalsIgnoreCase(dns.getString("status").trim())) {
            servicesWithDnsIssues.add(serviceName);
        }

        // 检查日志
        if (!"".equals(healthObject.getJSONObject("logs").getString("value").trim())) {
            servicesWithLogIssues.add(serviceName);
        }
    }

    private Map<String, Object> createMetric(String name, int affectedCount, String description, List<String> services) {
        Map<String, Object> metric = new HashMap<>();
        metric.put("name", name);
        metric.put("affectedCount", affectedCount);
        metric.put("description", description);
        metric.put("services", services);
        return metric;
    }
    @GetMapping("/getEventDetailByService")
    @OperationLog("获取服务事件详情")
    public CustomResult getEventDetailByService(@RequestParam String recordId, @RequestParam String servicename) {
        // 获取记录信息
        ChaosExerciseRecords chaosExerciseRecords = chaosExerciseRecordsService.getById(recordId);
        if (chaosExerciseRecords == null) {
            log.warn("记录不存在, recordId={}", recordId);
            return CustomResult.fail("记录不存在");
        }

        // 获取记录状态
        Integer status = chaosExerciseRecords.getRecordStatus();
        log.info("获取服务事件详情: recordId={}, servicename={}, status={}", recordId, servicename, status);

        // 获取计划信息以检查是否有压测
        Planv2 plan = planv2Service.getById(chaosExerciseRecords.getPlanId());
        boolean hasLoad = plan != null && plan.getLoadId() != null && !plan.getLoadId().isEmpty();
        log.info("是否包含压测: hasLoad={}", hasLoad);

        if (hasLoad) {
            // --- 有压测模式的状态处理 ---
            switch (status) {
                case 0: // 未运行
                    log.info("有压测-未运行状态");
                    return CustomResult.fail("演练尚未开始，请等待");

                case 1: // 初始压测
                    log.info("有压测-初始压测状态");
                    // 初始压测阶段通常没有故障注入，但可以显示压测期间的服务状态
                    try {
                        Date start = chaosExerciseRecords.getStartTime();
                        Date end = new Date(); // 使用当前时间
                        if (start == null) {
                            log.error("记录开始时间为空");
                            return CustomResult.fail("记录开始时间异常");
                        }
                        // 注意：此时可能没有故障注入前/中/后的概念，但可以展示当前时间段的状态
                        // 这里暂时复用 getServiceDetailsByTimeRange，但可能需要调整其内部逻辑或创建新方法
                        log.warn("初始压测阶段调用 getServiceDetailsByTimeRange，可能只反映部分时间段");
                        return getServiceDetailsByTimeRange(chaosExerciseRecords, start, end, servicename);
                    } catch (Exception e) {
                        log.error("获取服务详情失败 (初始压测)", e);
                        return CustomResult.fail("获取服务详情失败: " + e.getMessage());
                    }

                case 2: // 故障注入
                case 3: // 收尾压测
                    log.info("有压测-故障注入或收尾压测状态");
                    try {
                        Date start = chaosExerciseRecords.getStartTime();
                        Date end = new Date(); // 使用当前时间作为结束时间

                        if (start == null) {
                            log.error("记录开始时间为空");
                            return CustomResult.fail("记录开始时间异常");
                        }

                        return getServiceDetailsByTimeRange(chaosExerciseRecords, start, end, servicename);
                    } catch (Exception e) {
                        log.error("获取服务详情失败 (进行中)", e);
                        return CustomResult.fail("获取服务详情失败: " + e.getMessage());
                    }

                case 4: // 执行成功
                    log.info("有压测-执行成功状态");
                    try {
                        Date start = chaosExerciseRecords.getStartTime();
                        Date end = chaosExerciseRecords.getEndTime();

                        if (start == null || end == null) {
                            log.error("记录时间异常: startTime={}, endTime={}", start, end);
                            return CustomResult.fail("记录时间异常");
                        }

                        return getServiceDetailsByTimeRange(chaosExerciseRecords, start, end, servicename);
                    } catch (Exception e) {
                        log.error("获取服务详情失败 (成功)", e);
                        return CustomResult.fail("获取服务详情失败: " + e.getMessage());
                    }

                case 5: // 执行失败
                    log.info("有压测-执行失败状态");
                    if (chaosExerciseRecords.getStartTime() == null || chaosExerciseRecords.getEndTime() == null) {
                        log.error("执行失败且时间异常: startTime={}, endTime={}",
                                chaosExerciseRecords.getStartTime(), chaosExerciseRecords.getEndTime());
                        // 即使时间异常，也尝试获取可能存在的部分数据
                        Date start = chaosExerciseRecords.getStartTime() != null ? chaosExerciseRecords.getStartTime() : new Date(0); // 使用一个默认的早时间
                        Date end = chaosExerciseRecords.getEndTime() != null ? chaosExerciseRecords.getEndTime() : new Date(); // 使用当前时间
                         log.warn("执行失败且时间异常，尝试获取部分时间段数据");
                         try {
                            return getServiceDetailsByTimeRange(chaosExerciseRecords, start, end, servicename);
                         } catch (Exception e) {
                             log.error("获取服务详情失败 (失败且时间异常)", e);
                             return CustomResult.fail("获取服务详情失败: " + e.getMessage());
                         }
                    }
                    try {
                        return getServiceDetailsByTimeRange(chaosExerciseRecords,
                                chaosExerciseRecords.getStartTime(),
                                chaosExerciseRecords.getEndTime(),
                                servicename);
                    } catch (Exception e) {
                        log.error("获取服务详情失败 (失败)", e);
                        return CustomResult.fail("获取服务详情失败: " + e.getMessage());
                    }

                default:
                    log.warn("未知的记录状态: {}", status);
                    return CustomResult.fail("未知的记录状态");
            }
        } else {
            // --- 无压测模式的状态处理 ---
            switch (status) {
                case 0: // 未运行
                    log.info("无压测-未运行状态");
                    return CustomResult.fail("演练尚未开始，请等待");

                case 1: // 故障注入中
                    log.info("无压测-故障注入状态");
                    try {
                        Date start = chaosExerciseRecords.getStartTime();
                        Date end = new Date(); // 使用当前时间作为结束时间

                        if (start == null) {
                            log.error("记录开始时间为空");
                            return CustomResult.fail("记录开始时间异常");
                        }

                        return getServiceDetailsByTimeRange(chaosExerciseRecords, start, end, servicename);
                    } catch (Exception e) {
                        log.error("获取服务详情失败 (进行中)", e);
                        return CustomResult.fail("获取服务详情失败: " + e.getMessage());
                    }

                case 2: // 执行成功
                    log.info("无压测-执行成功状态");
                    try {
                        Date start = chaosExerciseRecords.getStartTime();
                        Date end = chaosExerciseRecords.getEndTime();

                        if (start == null || end == null) {
                            log.error("记录时间异常: startTime={}, endTime={}", start, end);
                            return CustomResult.fail("记录时间异常");
                        }

                        return getServiceDetailsByTimeRange(chaosExerciseRecords, start, end, servicename);
                    } catch (Exception e) {
                        log.error("获取服务详情失败 (成功)", e);
                        return CustomResult.fail("获取服务详情失败: " + e.getMessage());
                    }

                case 3: // 执行失败
                    log.info("无压测-执行失败状态");
                     if (chaosExerciseRecords.getStartTime() == null || chaosExerciseRecords.getEndTime() == null) {
                        log.error("执行失败且时间异常: startTime={}, endTime={}",
                                chaosExerciseRecords.getStartTime(), chaosExerciseRecords.getEndTime());
                        // 即使时间异常，也尝试获取可能存在的部分数据
                        Date start = chaosExerciseRecords.getStartTime() != null ? chaosExerciseRecords.getStartTime() : new Date(0); // 使用一个默认的早时间
                        Date end = chaosExerciseRecords.getEndTime() != null ? chaosExerciseRecords.getEndTime() : new Date(); // 使用当前时间
                         log.warn("执行失败且时间异常，尝试获取部分时间段数据");
                         try {
                            return getServiceDetailsByTimeRange(chaosExerciseRecords, start, end, servicename);
                         } catch (Exception e) {
                             log.error("获取服务详情失败 (失败且时间异常)", e);
                             return CustomResult.fail("获取服务详情失败: " + e.getMessage());
                         }
                    }
                    try {
                        return getServiceDetailsByTimeRange(chaosExerciseRecords,
                                chaosExerciseRecords.getStartTime(),
                                chaosExerciseRecords.getEndTime(),
                                servicename);
                    } catch (Exception e) {
                        log.error("获取服务详情失败 (失败)", e);
                        return CustomResult.fail("获取服务详情失败: " + e.getMessage());
                    }

                default:
                    log.warn("未知的记录状态: {}", status);
                    return CustomResult.fail("未知的记录状态");
            }
        }
    }

    private CustomResult getServiceDetailsByTimeRange(ChaosExerciseRecords chaosExerciseRecords, 
            Date start, Date end, String servicename) {
        try {
            // 创建服务状态数据
            Map<String, Map<String, String>> serviceDetails = new HashMap<>();
            
            // 获取计划的持续时间和缓冲时间
            Planv2 plan = planv2Service.getById(chaosExerciseRecords.getPlanId());
            String duration = plan.getDuration().replace("s", "");
            long durationTime = Long.parseLong(duration);
            
            // 缓冲时间设置，默认为持续时间的20%（可根据实际需求调整）
            long bufferTime = (long)(durationTime * 0.2) * 1000;
            
            long startTime = start.getTime();
            
            // 1. 故障注入前状态 - 考虑第一次压测前的稳定状态
            long beforeStartTime = startTime;
            long beforeEndTime = startTime + durationTime * 1000 - bufferTime;
            JSONObject beforeHealthObject = fetchFirstFilteredHealthData(beforeStartTime, beforeEndTime, servicename);
            if (beforeHealthObject != null) {
                Map<String, String> beforeMetrics = extractMetrics(beforeHealthObject);
                serviceDetails.put("故障注入前", beforeMetrics);
            }
            
            // 2. 故障注入中状态 - 加入缓冲时间，确保捕获压测过程中的真实状态
            long duringStartTime = startTime + durationTime * 1000 + bufferTime;
            long duringEndTime = duringStartTime + durationTime * 1000 - bufferTime*2;
            JSONObject duringHealthObject = fetchFirstFilteredHealthData(duringStartTime, duringEndTime, servicename);
            if (duringHealthObject != null) {
                Map<String, String> duringMetrics = extractMetrics(duringHealthObject);
                serviceDetails.put("故障注入中", duringMetrics);
            }
            
            // 3. 故障恢复后状态 - 加入缓冲时间，确保系统已从压测中恢复
            long afterStartTime = duringEndTime + bufferTime*2;
            long afterEndTime = afterStartTime + durationTime * 1000;
            
            // 确保不超过指定的结束时间
            if (afterEndTime > end.getTime()) {
                afterEndTime = end.getTime();
            }
            
            // 只有当有足够的时间来收集恢复数据时才执行
            if (afterEndTime - afterStartTime > bufferTime) {
                JSONObject afterHealthObject = fetchFirstFilteredHealthData(afterStartTime, afterEndTime, servicename);
                if (afterHealthObject != null) {
                    Map<String, String> afterMetrics = extractMetrics(afterHealthObject);
                    serviceDetails.put("故障恢复后", afterMetrics);
                }
            }
            
            if (serviceDetails.isEmpty()) {
                return CustomResult.fail("未找到服务相关数据");
            }
            
            return CustomResult.ok(serviceDetails);
        } catch (Exception e) {
            log.error("获取服务详情失败", e);
            return CustomResult.fail("获取服务详情失败: " + e.getMessage());
        }
    }

    private Map<String, String> extractMetrics(JSONObject healthObject) {
        Map<String, String> metrics = new HashMap<>();
        
        // 辅助方法，处理空值
        metrics.put("Error", getValueOrDefault(healthObject.getJSONObject("errors").getString("value")));
        metrics.put("Latency", getValueOrDefault(healthObject.getJSONObject("latency").getString("value")));
        metrics.put("Upstreams", getValueOrDefault(healthObject.getJSONObject("upstreams").getString("value")));
        metrics.put("Instances", getValueOrDefault(healthObject.getJSONObject("instances").getString("value")));
        metrics.put("Restarts", getValueOrDefault(healthObject.getJSONObject("restarts").getString("value")));
        metrics.put("CPU", getValueOrDefault(healthObject.getJSONObject("cpu").getString("value")));
        metrics.put("Memory", getValueOrDefault(healthObject.getJSONObject("memory").getString("value")));
        metrics.put("I/O Load", getValueOrDefault(healthObject.getJSONObject("disk_io_load").getString("value")));
        metrics.put("Disk", getValueOrDefault(healthObject.getJSONObject("disk_usage").getString("value")));
        metrics.put("Network", getValueOrDefault(healthObject.getJSONObject("network").getString("value")));
        metrics.put("DNS", getValueOrDefault(healthObject.getJSONObject("dns").getString("value")));
        metrics.put("Logs", getValueOrDefault(healthObject.getJSONObject("logs").getString("value")));
        
        return metrics;
    }

    private String getValueOrDefault(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value.trim();
    }

    @GetMapping("/getTraceSummarize")
    @OperationLog("获取追踪摘要")
    public CustomResult getTraceSummarize(@RequestParam String recordId) {
        try {
            // 构建查询参数
            Map<String, String> query = new HashMap<>();
            query.put("view", "errors");

            // 将 query 转换为 JSON 字符串并进行 URL 编码
            String queryJson = new ObjectMapper().writeValueAsString(query);
            String encodedQuery = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());
            ChaosExerciseRecords chaosExerciseRecords =chaosExerciseRecordsService.getById(recordId);
            Date start = chaosExerciseRecords.getStartTime();
            Date end = chaosExerciseRecords.getEndTime();
            long startTime = start.getTime();
            long endTime = end.getTime();

            // 构建完整的 URL
            String url = String.format("%s%s/overview/traces?query=%s&from=%s&to=%s",
                    corootUrl, projectId, encodedQuery, startTime, endTime);
            // 创建 RestTemplate 实例
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);

            // 创建请求实体
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            // 发送 GET 请求，使用 String.class 作为响应类型
            ResponseEntity<Object> response = restTemplate.exchange(
                    new URI(url),
                    HttpMethod.GET,
                    requestEntity,
                    Object.class
            );

            return new CustomResult(200, "Success", response.getBody());

        } catch (Exception e) {
            return new CustomResult(500, "Failed to get trace: " + null, null);
        }
    }


    @GetMapping("/getTraceDetail")
    public CustomResult getTraceDetail(@RequestParam String traceId) {
        try {
            // 构建查询参数
            Map<String, String> query = new HashMap<>();
            query.put("view", "traces");
            query.put("trace_id", traceId);

            // 将 query 转换为 JSON 字符串并进行 URL 编码
            String queryJson = new ObjectMapper().writeValueAsString(query);
            String encodedQuery = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());

            // 构建完整的 URL
            String url = String.format("%s%s/overview/traces?query=%s",
                    corootUrl, projectId, encodedQuery);

            // 创建 RestTemplate 实例
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);

            // 创建请求实体
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            // 发送 GET 请求，使用 String.class 作为响应类型
            ResponseEntity<Object> response = restTemplate.exchange(
                    new URI(url),
                    HttpMethod.GET,
                    requestEntity,
                    Object.class
            );

            return new CustomResult(200, "Success", response.getBody());

        } catch (Exception e) {
            return new CustomResult(500, "Failed to get trace detail: " + null, null);
        }
    }
    @GetMapping("/getLogs")
    @OperationLog("获取日志")
    public CustomResult getLogs(@RequestParam String recordId,
                                @RequestParam String namespace,
                                @RequestParam String serviceName) {
        try {
            // 构建查询参数
            Map<String, String> query = new HashMap<>();
            query.put("source", "agent");
            query.put("view", "patterns");

            // 将 query 转换为 JSON 字符串并进行 URL 编码
            String queryJson = new ObjectMapper().writeValueAsString(query);
            String encodedQuery = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());
            ChaosExerciseRecords chaosExerciseRecords =chaosExerciseRecordsService.getById(recordId);
            Date start = chaosExerciseRecords.getStartTime();
            Date end = chaosExerciseRecords.getEndTime();
            long startTime = start.getTime();
            long endTime = end.getTime();

            // 构建完整的 URL
            String url = String.format("%s%s/app/%s:Deployment:%s/logs?query=%s&from=%s&to=%s",
                    corootUrl,
                    projectId,
                    namespace,
                    serviceName,
                    encodedQuery,
                    startTime,
                    endTime);
            System.out.println(url);
            // 创建 RestTemplate 实例
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);

            // 创建请求实体
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            // 发送 GET 请求
            ResponseEntity<Object> response = restTemplate.exchange(
                    new URI(url),
                    HttpMethod.GET,
                    requestEntity,
                    Object.class
            );

            return new CustomResult(200, "Success", response.getBody());

        } catch (Exception e) {
            return new CustomResult(500, "Failed to get logs: " + null, null);
        }
    }
    @GetMapping("/getServiceLogsList")
    @OperationLog("获取服务日志列表")
    public CustomResult getServiceLogsList(@RequestParam String recordId) {
        try {
            ChaosExerciseRecords chaosExerciseRecords =chaosExerciseRecordsService.getById(recordId);
            Date start = chaosExerciseRecords.getStartTime();
            Date end = chaosExerciseRecords.getEndTime();
            long startTime = start.getTime();
            long endTime = end.getTime();

            // 需要获取到对应的namespace
            String namespace = applicationService.getById(planv2Service.getById(chaosExerciseRecords.getPlanId()).getApplicationId()).getNamespace();
            // 构建 URL
            String url = String.format("%s%s/overview/health?query=&from=%s&to=%s",
                    corootUrl,
                    projectId,
                    startTime,
                    endTime);

            // 创建 RestTemplate 实例
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);

            // 创建请求实体
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            // 发送 GET 请求获取原始数据
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    new URI(url),
                    HttpMethod.GET,
                    requestEntity,
                    JsonNode.class
            );

            // 处理响应数据
            List<Map<String, Object>> resultList = new ArrayList<>();
            JsonNode healthArray = response.getBody().get("data").get("health");

            for (JsonNode item : healthArray) {
                String id = item.get("id").asText();
                String[] idParts = id.split(":");
                
                // 检查是否有足够的部分且namespace匹配
                if (idParts.length >= 3 && idParts[0].equals(namespace)) {
                    String serviceName = idParts[2];  // 获取第三部分作为 serviceName
                    
                    // 获取 logs 数据
                    JsonNode logs = item.get("logs");

                    Map<String, Object> serviceInfo = new HashMap<>();
                    serviceInfo.put("serviceName", serviceName);
                    serviceInfo.put("logs", logs);

                    resultList.add(serviceInfo);
                }
            }

            return new CustomResult(200, "Success", resultList);

        } catch (Exception e) {
            return new CustomResult(500, "Failed to get service logs list: " + null, null);
        }
    }


    @GetMapping("/getPerformanceMetrics")
    @OperationLog("获取性能指标")
    public CustomResult getPerformanceMetrics(@RequestParam String recordId) {
        try {
            Map<String, Object> metrics = loadTaskService.getPerformanceMetricsByRecordId(recordId);
            return new CustomResult(20000, "Success", metrics);
        } catch (Exception e) {
            return new CustomResult(50000, null, null);
        }
    }

    @PostMapping("/list")
    @OperationLog("查询演练记录列表")
    public CustomResult queryRecordList(@RequestBody Recordv2QueryCriteria criteria) {
        try {
            IPage<RecordListDTO> page = chaosExerciseRecordsService.queryRecordList(criteria);
            return CustomResult.ok(page);
        } catch (Exception e) {
            log.error("查询演练记录列表失败", e);
            return CustomResult.fail("查询演练记录列表失败: " + null);
        }
    }

    @GetMapping("/status/count")
    @OperationLog("获取演练记录状态统计")
    public CustomResult countRecordStatus() {
        Map<Integer, Long> statusCount = chaosExerciseRecordsService.countByRecordStatus();
        
        // 创建新的数据结构，包含状态描述和计数
        Map<String, Object> result = new HashMap<>();
        
        // 添加旧状态计数
        Map<String, Object> oldStatusMap = new HashMap<>();
        oldStatusMap.put("0", statusCount.getOrDefault(0, 0L)); // 未运行
        oldStatusMap.put("1", statusCount.getOrDefault(1, 0L)); // 故障注入中
        oldStatusMap.put("2", statusCount.getOrDefault(2, 0L)); // 执行成功
        oldStatusMap.put("3", statusCount.getOrDefault(3, 0L)); // 执行失败
        result.put("noLoadStatus", oldStatusMap);
        
        // 添加新状态计数（将旧状态映射到新状态）
        Map<String, Object> newStatusMap = new HashMap<>();
        newStatusMap.put("0", statusCount.getOrDefault(0, 0L)); // 未运行
        newStatusMap.put("1", statusCount.getOrDefault(1, 0L)); // 初始压测中
        newStatusMap.put("2", 0L); // 故障注入中 - 需要从服务获取
        newStatusMap.put("3", 0L); // 收尾压测中 - 需要从服务获取
        newStatusMap.put("4", statusCount.getOrDefault(2, 0L)); // 执行成功
        newStatusMap.put("5", statusCount.getOrDefault(3, 0L)); // 执行失败
        result.put("loadStatus", newStatusMap);
        
        return CustomResult.ok(result);
    }

    public JSONObject fetchFirstFilteredHealthData(long startTime, long endTime, String servicename) {
        // 构建完整的 URL
        String url = String.format("%s%s/overview/health?query=&from=%d&to=%d",
                corootUrl, projectId, startTime, endTime);

        // 创建 RestTemplate 实例
        RestTemplate restTemplate = new RestTemplate();
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        headers.set("Accept", "*/*");
        HttpEntity<?> entity = new HttpEntity<>(headers);
        // 发起请求
        ResponseEntity<JSONObject> response = restTemplate.exchange(url, HttpMethod.GET, entity, JSONObject.class);
        JSONObject jsonObject = response.getBody();
        if (jsonObject == null) {
            throw new RuntimeException("获取响应数据为空");
        }

        JSONObject data = jsonObject.getJSONObject("data");
        if (data == null) {
            throw new RuntimeException("响应数据中缺少 data 字段");
        }
        // 获取 "health" 数组
        JSONArray healthArray = data.getJSONArray("health");

        // 遍历 "health" 数组，查找第一个符合条件的对象
        for (int i = 0; i < healthArray.size(); i++) {
            JSONObject healthObj = healthArray.getJSONObject(i);

            // 获取 "id" 字段
            String id = healthObj.getString("id");

            // 如果 "id" 以 servicename 结尾，返回该对象
            if (id != null && id.endsWith(servicename)) {
                return healthObj; // 直接返回第一个符合条件的对象
            }
        }

        // 如果没有找到符合条件的对象，返回 null
        return null;
    }

}
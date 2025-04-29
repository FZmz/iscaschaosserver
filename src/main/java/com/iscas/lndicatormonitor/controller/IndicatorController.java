package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/indicator")
public class IndicatorController {

    private static final String DATA_FILE_PATH = "./src/main/resources/indicator/data.json";

    // 获取所有故障及其对应指标名
    @GetMapping("/getIndexEntry")
    @OperationLog("获取指标列表")
    public CustomResult getIndexEntry() {
        try {
            // Read JSON file
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> data = mapper.readValue(new File(DATA_FILE_PATH), List.class);

            // Return all entries
            return new CustomResult(200, "Success", data);
        } catch (Exception e) {
            return new CustomResult(500, "Failed to load data", e);
        }
    }

    // 根据故障名获取推荐指标
    @GetMapping("/getRecommendedMetrics")
    @OperationLog("获取指标详情")
    public CustomResult getRecommendedMetrics(@RequestParam String failureName) {
        try {
            // Read JSON file
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> data = mapper.readValue(new File(DATA_FILE_PATH), List.class);

            // Filter the data by failureName
            List<Map<String, Object>> filtered = data.stream()
                    .filter(entry -> failureName.equals(entry.get("failureName")))
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                return new CustomResult(404, "Failure name not found", null);
            }

            // Extract recommended metrics for the matched failureName
            List<String> recommendedMetrics = (List<String>) filtered.get(0).get("recommendedMetrics");

            return new CustomResult(200, "Success", recommendedMetrics);
        } catch (Exception e) {
            return new CustomResult(500, "Failed to load data", null);
        }
    }

    // 模拟的 PodPhase 数据
    private static final Map<Long, String> POD_PHASE_METRICS = new HashMap<Long, String>() {
        {
            put(1697640000000L, "Running");
            put(1697643600000L, "Pending");
            put(1697647200000L, "Running");
            put(1697650800000L, "Failed");
            put(1697654400000L, "Running");
        }
    };

    // 模拟的 ContainerRestartCount 数据
    private static final Map<Long, Integer> CONTAINER_RESTART_COUNT_METRICS = new HashMap<Long, Integer>() {
        {
            put(1697640000000L, 0);
            put(1697643600000L, 1);
            put(1697647200000L, 1);
            put(1697650800000L, 2);
            put(1697654400000L, 3);
        }
    };

    /**
     * 根据服务名、指标名、开始时间戳和结束时间戳获取指标值
     */
    @GetMapping("/getMetrics")
    @OperationLog("获取指标详情")
    public CustomResult getMetrics(@RequestParam String serviceName,
            @RequestParam String metricName,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        try {
            // 根据指标名选择数据源
            Map<Long, ?> metrics = getMetricData(metricName);
            if (metrics == null) {
                return new CustomResult(404, "Metric not found for the given name", null);
            }

            // 筛选符合时间范围的指标数据
            Map<Long, ?> filteredMetrics = metrics.entrySet().stream()
                    .filter(entry -> entry.getKey() >= startTime && entry.getKey() <= endTime)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // 返回结果
            if (filteredMetrics.isEmpty()) {
                return new CustomResult(40000, "No metrics found in the given time range", null);
            }

            return new CustomResult(20000, "Success", filteredMetrics);
        } catch (Exception e) {
            return new CustomResult(50000, "Error fetching metrics", null);
        }
    }

    /**
     * 根据指标名返回对应的模拟数据
     */
    private Map<Long, ?> getMetricData(String metricName) {
        switch (metricName) {
            case "PodPhase":
                return POD_PHASE_METRICS;
            case "ContainerRestartCount":
                return CONTAINER_RESTART_COUNT_METRICS;
            default:
                return null;
        }
    }

    @GetMapping("/list")
    @OperationLog("获取指标列表")
    public CustomResult getIndicators() {
        // 获取指标列表逻辑
        return new CustomResult(20000, "获取成功", null);
    }

    @GetMapping("/get/{id}")
    @OperationLog("获取指标详情")
    public CustomResult getIndicatorById(@PathVariable Integer id) {
        // 获取指标详情逻辑
        return new CustomResult(20000, "获取成功", null);
    }

}

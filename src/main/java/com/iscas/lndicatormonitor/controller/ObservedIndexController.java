package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.IndexRecommend.Faults;
import com.iscas.lndicatormonitor.domain.IndexRecommend.RecommendedMetrics;
import com.iscas.lndicatormonitor.dto.indexRecommend.PrometheusProcessorDTO;
import com.iscas.lndicatormonitor.handler.MetricSourceHandler;
import com.iscas.lndicatormonitor.service.FaultMetricMappingService;
import com.iscas.lndicatormonitor.service.FaultsService;
import com.iscas.lndicatormonitor.service.RecommendedMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/observedIndex")
public class ObservedIndexController {


    @Autowired
    private  FaultsService faultsService;
    @Autowired
    private  FaultMetricMappingService faultMetricMappingService;
    @Autowired
    private  RecommendedMetricsService recommendedMetricsService;;

    @Autowired
    private List<MetricSourceHandler> handlers;

    @GetMapping("/getRecommendMetrics")
    @OperationLog("获取推荐指标")
    public CustomResult getRecommendMetrics(@RequestParam String faultName) {
        // Step 1: 根据故障名称获取 faultId
        Faults fault = faultsService.getFaultByName(faultName);
        if (fault == null) {
            return CustomResult.fail("found no faults");
        }

        Integer faultId = fault.getId();

        // Step 2: 根据 faultId 获取对应的 metricId 列表
        List<Integer> metricIds = faultMetricMappingService.getMetricIdsByFaultId(faultId);
        if (metricIds.isEmpty()) {
            return CustomResult.fail("found no metrics");
        }

        // Step 3: 根据 metricId 列表获取对应的观测指标
        List<RecommendedMetrics> metrics = recommendedMetricsService.getMetricsByIds(metricIds);

        return CustomResult.ok(metrics);
    }

    @PostMapping("/getRecommendObservedIndex")
    @OperationLog("获取观测指标")
    public CustomResult processObservedIndex(@RequestBody PrometheusProcessorDTO dto) {
        try {
            // Step 1: 获取指标详情信息
            List<RecommendedMetrics> metricsList = recommendedMetricsService.getMetricsByMetricName(dto.getIndexName());
            if (metricsList.isEmpty()) {
                return  CustomResult.fail("Metric details not found for indexName: " + dto.getIndexName());
            }
            RecommendedMetrics metric = metricsList.get(0); // 假设 metricName 是唯一的
            String tool = metric.getTool();
            // Step 2: 根据工具选择处理器
            MetricSourceHandler handler = handlers.stream()
                    .filter(h -> h.getClass().getSimpleName().toLowerCase().contains(tool.toLowerCase()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No handler found for tool: " + tool));
            // Step 3: 调用处理器进行处理
            Object result = handler.handle(dto);
            return CustomResult.ok(result);

        } catch (Exception e) {
            return CustomResult.fail("Error processing observed index: " + null);
        }
    }

    // 获取所有指标名称
    @GetMapping("/getAllIndexName")
    @OperationLog("获取所有指标名称")
    public CustomResult getAllIndexName() {
        List<String> allIndexName = recommendedMetricsService.list().stream()
                .map(RecommendedMetrics::getMetricName)
                .collect(Collectors.toList()); // 触发终端操作，将结果收集为 List
        return CustomResult.ok(allIndexName);
    }

    @GetMapping("/getAllIndex")
    @OperationLog("获取所有指标")
    public CustomResult getAllIndex(@RequestParam int page,
                                    @RequestParam int size) {
        // 构造分页对象
        Page<RecommendedMetrics> pageObj = new Page<>(page, size);
        // 分页查询，返回 IPage 对象
        IPage<RecommendedMetrics> resultPage = recommendedMetricsService.page(pageObj);
        return CustomResult.ok(resultPage);
    }
}


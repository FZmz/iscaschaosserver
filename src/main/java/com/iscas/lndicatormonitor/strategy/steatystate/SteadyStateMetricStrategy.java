package com.iscas.lndicatormonitor.strategy.steatystate;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SteadyStateMetricStrategy implements IMetricStrategy {

    private final Map<Integer, Map<String, IMetricItemStrategy>> typeStrategyMap;

    public SteadyStateMetricStrategy() {
        // 初始化策略映射
        this.typeStrategyMap = new HashMap<>();

        // 全局稳态策略
        Map<String, IMetricItemStrategy> globalStrategies = new HashMap<>();
        globalStrategies.put("GlobalSuccessRate", new GlobalSuccessRateMetricStrategy());
        globalStrategies.put("GlobalErrorRate", new GlobalErrorRateMetricStrategy());
        globalStrategies.put("GlobalThroughput", new GlobalThroughputMetricStrategy());
        globalStrategies.put("GlobalRequestLatency", new GlobalRequestLatencyMetricStrategy());

        // 局部稳态策略
        Map<String, IMetricItemStrategy> partialStrategies = new HashMap<>();
        partialStrategies.put("ErrorRate", new ErrorRateMetricStrategy());
        partialStrategies.put("P25Latency", new P25LatencyMetricStrategy());
        partialStrategies.put("P50Latency", new P50LatencyMetricStrategy());
        partialStrategies.put("P75Latency", new P75LatencyMetricStrategy());
        partialStrategies.put("P95Latency", new P95LatencyMetricStrategy());
        partialStrategies.put("P99Latency", new P99LatencyMetricStrategy());
        partialStrategies.put("Availability", new AvailabilityMetricStrategy());
        partialStrategies.put("Throughput", new ThroughputMetricStrategy());

        // 将策略注册到类型映射表
        this.typeStrategyMap.put(1, globalStrategies); // 0 表示全局稳态
        this.typeStrategyMap.put(2, partialStrategies); // 1 表示局部稳态
    }

    @Override
    public Object compute(int type, String steadyStateName, JsonNode rootNode) {
        // 根据类型获取对应的策略映射表
        Map<String, IMetricItemStrategy> strategyMap = typeStrategyMap.get(type);
        if (strategyMap == null) {
            throw new IllegalArgumentException("未知的稳态类型：" + type);
        }

        // 根据稳态名称获取具体的策略
        IMetricItemStrategy itemStrategy = strategyMap.get(steadyStateName);
        if (itemStrategy == null) {
            throw new IllegalArgumentException("未知的稳态名称：" + steadyStateName);
        }

        // 使用具体策略计算稳态值
        return itemStrategy.computeMetric(rootNode);
    }
}
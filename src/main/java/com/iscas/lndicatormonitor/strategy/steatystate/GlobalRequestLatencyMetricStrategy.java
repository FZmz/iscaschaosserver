package com.iscas.lndicatormonitor.strategy.steatystate;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalRequestLatencyMetricStrategy implements IMetricItemStrategy {

    @Override
    public String getMetricName() {
        return "Global Request Latency (ms)";
    }

    @Override
    public Object computeMetric(JsonNode rootNode) {
        JsonNode latencyData = rootNode.at("/data/reports/0/widgets/4/chart/series");
        if (!latencyData.isArray()) {
            return 0.0;
        }
        for (JsonNode series : latencyData) {
            String name = series.get("name").asText("");
            if ("p50".equalsIgnoreCase(name)) {
                JsonNode dataArray = series.get("data");
                double sum = 0;
                for (JsonNode val : dataArray) {
                    sum += val.asDouble();
                }
                double result = (sum / dataArray.size()) * 1000; // 转换为毫秒
                // 保留两位小数
                return Double.parseDouble(String.format("%.2f", result));
            }
        }
        return 0.0;
    }
}
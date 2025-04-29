package com.iscas.lndicatormonitor.strategy.steatystate;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalSuccessRateMetricStrategy implements IMetricItemStrategy {

    @Override
    public String getMetricName() {
        return "Business Overall Success Rate";
    }

    @Override
    public Object computeMetric(JsonNode rootNode) {
        JsonNode charts = rootNode.at("/data/reports/0/widgets/2/chart/series");
        if (!charts.isArray()) {
            return "100.00"; // 如果没有错误率数据，成功率为100%
        }
        for (JsonNode series : charts) {
            String name = series.get("name").asText("");
            if ("errors".equalsIgnoreCase(name)) {
                JsonNode dataArray = series.get("data");
                double sum = 0;
                for (JsonNode val : dataArray) {
                    sum += val.asDouble();
                }
                double avgErrorRate = sum / dataArray.size();
                double successRate = 100 - (avgErrorRate * 100); // 100% - 错误率%
                return String.format("%.2f", successRate);
            }
        }
        return "100.00"; // 默认成功率为100%
    }
}
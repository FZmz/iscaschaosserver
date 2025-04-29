package com.iscas.lndicatormonitor.strategy.steatystate;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorRateMetricStrategy implements IMetricItemStrategy {

    @Override
    public String getMetricName() {
        return "Average Error Rate";
    }

    @Override
    public Object computeMetric(JsonNode rootNode) {
        JsonNode charts = rootNode.at("/data/reports/0/widgets/2/chart/series");
        if (!charts.isArray()) {
            return "0.00";
        }
        for (JsonNode series : charts) {
            String name = series.get("name").asText("");
            if ("errors".equalsIgnoreCase(name)) {
                JsonNode dataArray = series.get("data");
                if (dataArray.isArray() && dataArray.size() > 0) {
                    double sum = 0;
                    for (JsonNode val : dataArray) {
                        sum += val.asDouble();
                    }
                    double avgErrorRate = sum / dataArray.size();
                    return String.format("%.2f", avgErrorRate * 100);
                }
            }
        }
        return "0.00";
    }
}
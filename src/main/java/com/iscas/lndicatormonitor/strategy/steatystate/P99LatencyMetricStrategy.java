package com.iscas.lndicatormonitor.strategy.steatystate;

import com.fasterxml.jackson.databind.JsonNode;
import com.iscas.lndicatormonitor.utils.SteadystateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class P99LatencyMetricStrategy implements IMetricItemStrategy {

    @Override
    public String getMetricName() {
        return "Average P99 Latency (ms)";
    }

    @Override
    public Object computeMetric(JsonNode rootNode) {
        log.info("开始计算 P99 延迟");

        JsonNode latencyData = rootNode.at("/data/reports/0/widgets/4/chart/series");
        if (!latencyData.isArray()) {
            log.warn("未找到延迟数据节点或数据格式不正确: {}", latencyData);
            return 0.0;
        }

        for (JsonNode series : latencyData) {
            String name = series.get("name").asText("");
            if ("p99".equalsIgnoreCase(name)) {
                JsonNode dataArray = series.get("data");
                double sum = 0;
                for (JsonNode val : dataArray) {
                    sum += val.asDouble();
                }
                double averageLatencyMs = (sum / dataArray.size()) * 1000; // 转换为毫秒
                double formattedAverage = formatToTwoDecimalPlaces(averageLatencyMs);

                log.info("成功计算 P99 延迟: 平均值={}ms", formattedAverage);
                return formattedAverage;
            }
        }

        log.warn("未找到 P99 数据系列");
        return 0.0;
    }

    private double formatToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
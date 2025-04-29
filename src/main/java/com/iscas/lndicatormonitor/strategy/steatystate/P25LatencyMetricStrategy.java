package com.iscas.lndicatormonitor.strategy.steatystate;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class P25LatencyMetricStrategy implements IMetricItemStrategy {

    @Override
    public String getMetricName() {
        return "Average P25 Latency (ms)";
    }

    @Override
    public Object computeMetric(JsonNode rootNode) {
        log.info("开始计算指标: {}", getMetricName());

        // 检查数据节点
        JsonNode latencyData = rootNode.at("/data/reports/0/widgets/4/chart/series");
        if (!latencyData.isArray() || latencyData.size() == 0) {
            log.warn("未找到延迟数据节点或数据为空: {}", latencyData);
            return 0.0;
        }

        // 遍历数据节点寻找 "p25" 数据
        for (JsonNode series : latencyData) {
            String name = series.get("name").asText("");
            log.info("检查延迟系列数据: name={}, series={}", name, series);

            if ("p25".equalsIgnoreCase(name)) {
                JsonNode dataArray = series.get("data");

                if (!dataArray.isArray() || dataArray.size() == 0) {
                    log.warn("P25 数据为空或格式不正确: {}", dataArray);
                    return 0.0;
                }

                // 计算平均值
                double sum = 0;
                for (JsonNode val : dataArray) {
                    sum += val.asDouble();
                }
                double averageLatencyMs = (sum / dataArray.size()) * 1000; // 转换为毫秒

                // 格式化为小数点后两位
                double formattedAverageLatencyMs = BigDecimal.valueOf(averageLatencyMs)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();

                log.info("成功计算 P25 延迟: 原始值 (ms)={}, 格式化值 (ms)={}", averageLatencyMs, formattedAverageLatencyMs);
                return formattedAverageLatencyMs;
            }
        }

        log.warn("未找到 P25 数据系列");
        return 0.0;
    }
}
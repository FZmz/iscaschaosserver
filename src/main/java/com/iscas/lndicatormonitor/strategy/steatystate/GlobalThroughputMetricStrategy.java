package com.iscas.lndicatormonitor.strategy.steatystate;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class GlobalThroughputMetricStrategy implements IMetricItemStrategy {

    @Override
    public String getMetricName() {
        return "Global Throughput";
    }

    @Override
    public Object computeMetric(JsonNode rootNode) {
        log.info("开始计算指标: {}", getMetricName());

        // 检查 thresholdNode 是否存在并是数组
        JsonNode thresholdNode = rootNode.at("/data/reports/0/widgets/2/chart/threshold/data");
        if (!thresholdNode.isArray() || thresholdNode.size() == 0) {
            log.warn("未找到 threshold 数据节点或数据为空: {}", thresholdNode);
            return 0.0;
        }

        // 计算平均值
        double sum = 0;
        for (JsonNode val : thresholdNode) {
            sum += val.asDouble();
        }
        double averageThreshold = sum / thresholdNode.size();

        // 格式化为小数点后两位
        double formattedAverageThreshold = BigDecimal.valueOf(averageThreshold)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        log.info("成功计算 threshold 平均值: 原始值={}, 格式化值={}", averageThreshold, formattedAverageThreshold);
        return formattedAverageThreshold;
    }
}
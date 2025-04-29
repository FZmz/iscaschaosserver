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
public class ThroughputMetricStrategy implements IMetricItemStrategy {

    @Override
    public String getMetricName() {
        return "Average Throughput";
    }

    @Override
    public Object computeMetric(JsonNode rootNode) {
        log.info("开始计算 Average Throughput");

        JsonNode throughputData = rootNode.at("/data/reports/0/widgets/2/chart/threshold/data");
        if (!throughputData.isArray() || throughputData.size() == 0) {
            log.warn("未找到 Throughput 数据节点或数据为空: {}", throughputData);
            return 0.0;
        }

        double sum = 0;
        for (JsonNode val : throughputData) {
            sum += val.asDouble();
        }
        double averageThroughput = sum / throughputData.size();
        double formattedAverage = formatToTwoDecimalPlaces(averageThroughput);

        log.info("成功计算 Average Throughput: 平均值={}", formattedAverage);
        return formattedAverage;
    }

    private double formatToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
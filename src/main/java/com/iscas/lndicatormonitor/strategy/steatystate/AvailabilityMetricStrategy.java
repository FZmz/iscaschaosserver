package com.iscas.lndicatormonitor.strategy.steatystate;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AvailabilityMetricStrategy implements IMetricItemStrategy {

    @Override
    public String getMetricName() {
        return "Average Availability Rate";
    }

    @Override
    public Object computeMetric(JsonNode rootNode) {
        log.info("开始计算指标: {}", getMetricName());

        // 获取目标路径数据
        JsonNode charts = rootNode.at("/data/reports/1/widgets/1/chart/series");
        if (!charts.isArray() || charts.size() == 0) {
            log.warn("未找到可用性数据节点或数据为空: {}", charts);
            return "100"; // 默认可用性为100%
        }

        // 遍历查找 name 为 "up" 的数据
        for (JsonNode series : charts) {
            String name = series.get("name").asText("");
            log.debug("检查可用性系列数据: name={}, series={}", name, series);

            if ("up".equalsIgnoreCase(name)) {
                JsonNode dataArray = series.get("data");

                if (!dataArray.isArray() || dataArray.size() == 0) {
                    log.warn("可用性 'up' 数据为空或格式不正确: {}", dataArray);
                    return "0"; // 数据为空时，返回0%
                }

                // 计算 "up" 为 1 的比例
                double total = dataArray.size();
                long upCount = 0;
                for (JsonNode val : dataArray) {
                    if (val.asInt() == 1) {
                        upCount++;
                    }
                }
                double availabilityRate = (upCount / total) * 100;

                log.info("成功计算可用性: 总计={}, up计数={}, 可用率={:.4f}%", total, upCount, availabilityRate);
                return String.format("%.2f", availabilityRate);
            }
        }

        log.warn("未找到 'up' 数据系列");
        return "0%"; // 如果未找到 "up" 数据系列，返回0%
    }
}
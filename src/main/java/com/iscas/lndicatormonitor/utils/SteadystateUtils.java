package com.iscas.lndicatormonitor.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;


//@Slf4j
@Component
public class SteadystateUtils {
    public double computeAverage(JsonNode rootNode, String dataPath, String targetSeries, double multiplier) {
        JsonNode seriesData = rootNode.at(dataPath);
        if (!seriesData.isArray()) {
//            log.warn("未找到数据节点或数据格式不正确: {}", seriesData);
            return 0.0;
        }

        for (JsonNode series : seriesData) {
            String name = series.get("name").asText("");
            if (targetSeries.equalsIgnoreCase(name)) {
                JsonNode dataArray = series.get("data");
                if (!dataArray.isArray() || dataArray.size() == 0) {
//                    log.warn("{} 数据为空或格式不正确: {}", targetSeries, dataArray);
                    return 0.0;
                }

                double sum = 0;
                for (JsonNode val : dataArray) {
                    sum += val.asDouble();
                }
                double average = (sum / dataArray.size()) * multiplier;

                // 格式化到小数点后两位
                double formattedAverage = BigDecimal.valueOf(average)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();

//                log.info("成功计算 {}: 原始值={}, 格式化值={}", targetSeries, average, formattedAverage);
                return formattedAverage;
            }
        }
//        log.warn("未找到目标系列数据: {}", targetSeries);
        return 0.0;
    }
    public String sayhello(){
        return "hello jojo";
    }
}

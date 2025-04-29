package com.iscas.lndicatormonitor.strategy.steatystate;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface IMetricItemStrategy {

    /**
     * 获取指标名称
     */
    String getMetricName();

    /**
     * 计算指标值
     *
     * @param rootNode Coroot API返回的JSON根节点
     * @return 指标结果
     */
    Object computeMetric(JsonNode rootNode);
}
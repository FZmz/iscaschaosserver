package com.iscas.lndicatormonitor.strategy.metricquery;


import java.util.Map;

public interface MetricQueryStrategy {
    /**
     * 生成 Prometheus 查询语句
     *
     * @param metricName 指标名称
=     * @param params     其他参数，例如 namespace、pod 等
     * @return 生成的查询语句
     */
    String getQuery(String metricName, Map<String, String> params);
}
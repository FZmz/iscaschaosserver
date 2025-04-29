package com.iscas.lndicatormonitor.strategy.metricquery;

import com.iscas.lndicatormonitor.utils.PrometheusUtil;

import java.util.Map;

public class ContainerNetworkDuplicationRateStrategy implements MetricQueryStrategy {

    @Override
    public String getQuery(String metricName, Map<String, String> params) {
        // 替代实现：假设没有直接的 retransmitted 指标，使用其他相关指标计算
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("(");
        queryBuilder.append("rate(container_network_transmit_errors_total");
        PrometheusUtil.appendLabelSelectors(queryBuilder, params);
        queryBuilder.append("[5m])");
        queryBuilder.append(" - ");
        queryBuilder.append("rate(container_network_transmit_packets_dropped_total");
        PrometheusUtil.appendLabelSelectors(queryBuilder, params);
        queryBuilder.append("[5m])");
        queryBuilder.append(") / ");
        queryBuilder.append("rate(container_network_transmit_packets_total");
        PrometheusUtil.appendLabelSelectors(queryBuilder, params);
        queryBuilder.append("[5m]) * 100");
        return queryBuilder.toString();
    }
}
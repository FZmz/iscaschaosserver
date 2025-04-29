package com.iscas.lndicatormonitor.strategy.metricquery;

import com.iscas.lndicatormonitor.utils.PrometheusUtil;

import java.util.Map;

public class ContainerNetworkPacketLossRateStrategy implements MetricQueryStrategy {

    @Override
    public String getQuery(String metricName, Map<String, String> params) {
        // 定义计算丢包率的查询语句
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("(");
        queryBuilder.append("rate(container_network_transmit_packets_dropped_total");
        PrometheusUtil.appendLabelSelectors(queryBuilder, params);
        queryBuilder.append("[5m])");
        queryBuilder.append(" / ");
        queryBuilder.append("rate(container_network_transmit_packets_total");
        PrometheusUtil.appendLabelSelectors(queryBuilder, params);
        queryBuilder.append("[5m])");
        queryBuilder.append(") * 100");
        return queryBuilder.toString();
    }
}
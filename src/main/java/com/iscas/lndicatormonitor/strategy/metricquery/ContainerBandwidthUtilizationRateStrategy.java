package com.iscas.lndicatormonitor.strategy.metricquery;

import com.iscas.lndicatormonitor.utils.PrometheusUtil;

import java.util.Map;

public class ContainerBandwidthUtilizationRateStrategy implements MetricQueryStrategy {


    private static final double BANDWIDTH_CAPACITY_MBPS = 1000 * 1000 * 1000 / 8; // 假设网络带宽为 1Gbps，单位为 Bps

    @Override
    public String getQuery(String metricName, Map<String, String> params) {
        // 定义计算带宽利用率的查询语句
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("(");
        queryBuilder.append("rate(container_network_transmit_bytes_total");
        PrometheusUtil.appendLabelSelectors(queryBuilder, params);
        queryBuilder.append("[5m])");
        queryBuilder.append(" + ");
        queryBuilder.append("rate(container_network_receive_bytes_total");
        PrometheusUtil.appendLabelSelectors(queryBuilder, params);
        queryBuilder.append("[5m])");
        queryBuilder.append(") / ");
        queryBuilder.append(BANDWIDTH_CAPACITY_MBPS); // 使用静态定义的带宽容量
        queryBuilder.append(" * 100");
        return queryBuilder.toString();
    }
}
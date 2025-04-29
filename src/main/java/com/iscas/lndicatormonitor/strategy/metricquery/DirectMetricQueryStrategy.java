package com.iscas.lndicatormonitor.strategy.metricquery;

import com.iscas.lndicatormonitor.utils.PrometheusUtil;

import java.util.Map;

public class DirectMetricQueryStrategy implements MetricQueryStrategy {
    @Override
    public String getQuery(String metricName,Map<String, String> params) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("sum(")
                .append(metricName);
        PrometheusUtil.appendLabelSelectors(queryBuilder, params);
        queryBuilder.append(")");
        return queryBuilder.toString();
    }
}
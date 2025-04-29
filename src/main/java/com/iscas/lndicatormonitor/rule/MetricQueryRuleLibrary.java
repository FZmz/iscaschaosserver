package com.iscas.lndicatormonitor.rule;


import com.iscas.lndicatormonitor.strategy.metricquery.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MetricQueryRuleLibrary {

    private Map<String, MetricQueryStrategy> strategyMap = new HashMap<>();

    public MetricQueryRuleLibrary() {
        // 初始化策略映射
        MetricQueryStrategy directStrategy = new DirectMetricQueryStrategy();
        // 直接处理的指标
        strategyMap.put("container_fs_read_seconds_total", directStrategy);
        strategyMap.put("container_fs_write_seconds_total", directStrategy);
        strategyMap.put("container_cpu_usage_seconds_total", directStrategy);
        strategyMap.put("container_memory_usage_bytes", directStrategy);
        strategyMap.put("node_resources_disk_read_bytes_total", directStrategy);
        strategyMap.put("node_resources_disk_written_bytes_total", directStrategy);
        strategyMap.put("node_net_received_bytes_total", directStrategy);
        strategyMap.put("node_net_transmitted_bytes_total", directStrategy);

        // 特殊处理的指标
        strategyMap.put("container_network_packet_loss_rate", new ContainerNetworkPacketLossRateStrategy());
        strategyMap.put("container_network_duplication_rate", new ContainerNetworkDuplicationRateStrategy());
        strategyMap.put("container_network_error_rate", new ContainerNetworkErrorRateStrategy());
        strategyMap.put("container_bandwidth_utilization_rate", new ContainerBandwidthUtilizationRateStrategy());
    }

    /**
     * 根据指标名、开始时间和参数，获取查询语句
     *
     * @param metricName 指标名称
     * @param params     其他参数，例如 namespace、pod 等
     * @return 生成的查询语句，如果未找到策略则返回 null
     */
    public String getQuery(String metricName, Map<String, String> params) {
        MetricQueryStrategy strategy = strategyMap.get(metricName);
        if (strategy != null) {
            return strategy.getQuery(metricName, params);
        } else {
            // 未找到对应的策略
            return null;
        }
    }
}
package com.iscas.lndicatormonitor.handler.IndexRecommend.Prometheus;

import com.iscas.lndicatormonitor.dto.indexRecommend.PrometheusProcessorDTO;
import com.iscas.lndicatormonitor.handler.MetricSourceHandler;
import com.iscas.lndicatormonitor.processor.IndexRecommend.prometheus.PrometheusNetworkProcessor;
import com.iscas.lndicatormonitor.processor.IndexRecommend.prometheus.PrometheusNonNetworkProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrometheusHandler implements MetricSourceHandler {

    private static final Logger log = LoggerFactory.getLogger(PrometheusHandler.class);
    @Autowired
    private PrometheusNetworkProcessor networkProcessor;

    @Autowired
    private PrometheusNonNetworkProcessor nonNetworkProcessor;

    @Override
    public Object handle(PrometheusProcessorDTO dto) {
        // 判断指标名是否包含 "net"
        if (dto.getIndexName().toLowerCase().contains("net") || dto.getIndexName().toLowerCase().contains("bandwidth")) {
            return networkProcessor.process(dto);
        } else {
            return nonNetworkProcessor.process(dto);
        }
    }
}
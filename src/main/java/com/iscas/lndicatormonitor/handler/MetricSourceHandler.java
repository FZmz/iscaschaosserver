package com.iscas.lndicatormonitor.handler;

import com.iscas.lndicatormonitor.dto.indexRecommend.PrometheusProcessorDTO;

public interface MetricSourceHandler {
    Object handle(PrometheusProcessorDTO dto) throws Exception;
}

package com.iscas.lndicatormonitor.dto.indexRecommend;

import lombok.Data;

@Data
public class PrometheusProcessorDTO {
    private String namespace;
    private String podName;
    private String instance;
    private long from;
    private long to;
    private String indexName;
}
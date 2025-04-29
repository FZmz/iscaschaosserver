package com.iscas.lndicatormonitor.dto.coroot;

import lombok.Data;

import java.util.List;

@Data
public class MetricResponse {
    private String ownerName;
//    private List<Float> metricValue;
    private StringBuilder report;
}

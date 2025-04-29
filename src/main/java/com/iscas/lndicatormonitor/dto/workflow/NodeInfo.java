package com.iscas.lndicatormonitor.dto.workflow;

import lombok.Data;

import java.util.List;

@Data
public class NodeInfo {
    private String nodeName;
    private String startTime;
    private String endTime;
    private String reason;
    private List<StepSpan> StepSpanList;
}

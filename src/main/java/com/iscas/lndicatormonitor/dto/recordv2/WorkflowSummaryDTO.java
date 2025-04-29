package com.iscas.lndicatormonitor.dto.recordv2;

import lombok.Data;

import java.util.List;

@Data
public class WorkflowSummaryDTO {
    private String workflowName;
    private List<NodeInfo> nodeInfoList;

    @Data
    public static class NodeInfo {
        private String nodeName;
        private String startTime;
        private String endTime;
        private String reason;
        private List<StepSpan> stepSpanList;
    }

    @Data
    public static class StepSpan {
        private String startTime;
        private String endTime;
    }
}
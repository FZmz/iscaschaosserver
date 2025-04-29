package com.iscas.lndicatormonitor.dto.workflow;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WorkflowSummary {
    private String workflowName;
    private List<NodeInfo> nodeInfoList;
    private Map<String, Object> pressTestInfo;

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
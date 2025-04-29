package com.iscas.lndicatormonitor.dto.workflow;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.iscas.lndicatormonitor.utils.RawJsonDeserializer;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class WorkflowData {
    private String workflowName;
    private List<NodeInfo> nodeInfoList;
    @JsonDeserialize(using = RawJsonDeserializer.class)
    private Object pressTestInfo;

    private String reason;
}

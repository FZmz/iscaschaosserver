package com.iscas.lndicatormonitor.dto.coroot;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 忽略所有 null 字段
public class TraceRequestQuery {
    private String view;
    private List<TraceRequestFilter> filters;
    @JsonProperty("trace_id")
    private String traceId;
}

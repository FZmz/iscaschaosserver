package com.iscas.lndicatormonitor.dto.traceRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class TraceRequest {
    private String url;
    private String method;
    private Map<String, Object> header;
    private Map<String,Object> data;
    private Map<String,Object> params;
    public TraceRequest() {

    }
}

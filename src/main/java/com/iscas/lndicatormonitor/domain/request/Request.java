package com.iscas.lndicatormonitor.domain.request;

import lombok.Data;

import java.util.Map;

@Data
public class Request {
    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> params;
    private Map<String, Object> body;
}

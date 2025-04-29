package com.iscas.lndicatormonitor.dto.request;

import lombok.Data;

@Data
public class RequestDetail {
    private String method;
    private String url;
    private String headers;
    private String body;
}

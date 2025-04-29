package com.iscas.lndicatormonitor.dto.request;

import lombok.Data;

@Data
public class ResponseDetail {
    private Integer status;
    private String response_time;
    private String body;
}

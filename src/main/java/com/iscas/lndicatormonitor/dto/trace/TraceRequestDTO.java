package com.iscas.lndicatormonitor.dto.trace;


import lombok.Data;

@Data
public class TraceRequestDTO {
    private String url;
    private String method;
    private Object query;
    private Long from;
    private Long to;
}
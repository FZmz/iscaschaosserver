package com.iscas.lndicatormonitor.dto.coroot;

import lombok.Data;

@Data
public class TraceRequestFilter {
    private String field;
    private String op;
    private String value;
}

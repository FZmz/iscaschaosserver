package com.iscas.lndicatormonitor.dto.coroot;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TraceResponse {
    private List<Object> traceList;
    private List<Object> rooterrorSpanList;
}

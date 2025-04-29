package com.iscas.lndicatormonitor.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemTopoRequest {
    private String applicationName;
    private String serviceTypeName;
    private String duration;
}

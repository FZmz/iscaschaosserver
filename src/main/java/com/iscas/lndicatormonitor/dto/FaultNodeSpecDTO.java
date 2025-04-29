package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.List;

@Data
public class FaultNodeSpecDTO {
    private int id;
    private String faultName;
    private String name;
    private String templateType;
    private String deadline;
    private List<Object> list;
}

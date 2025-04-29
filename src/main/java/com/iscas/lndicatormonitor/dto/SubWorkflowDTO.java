package com.iscas.lndicatormonitor.dto;

import lombok.Data;

@Data
public class SubWorkflowDTO {
    private Object workflow;
    private PressTestDTO k6;
}

package com.iscas.lndicatormonitor.dto.planv2;

import lombok.Data;

@Data
public class RecordInfoDTO {
    // startTime、endTime、recordStatus 、faulInjectObjectNum
    private String startTime;
    private String endTime;
    private Integer recordStatus;
    private Integer faulInjectObjectNum;
}

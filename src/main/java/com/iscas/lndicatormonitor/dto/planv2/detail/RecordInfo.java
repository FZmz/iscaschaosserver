package com.iscas.lndicatormonitor.dto.planv2.detail;

import lombok.Data;

import java.util.Date;

@Data
public class RecordInfo {
    private Integer id;
    private Integer pressStatus;
    private String recordStatus;
    private String player;
    private Date startTime;
    private Date endTime;
}
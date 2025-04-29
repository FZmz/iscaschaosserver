package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RecordSpecDTO {
    private String planName;
    private Date startTime;
    private Date endTime;
    private String duration;
    private int faultNum;
    private String player;
    private int pressStatus;
    private int playProgress;
    private PlayStatusInfo playStatusInfo;
}

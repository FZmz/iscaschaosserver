package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReportSpecDTO {
    private String planName;
    private String operator;
    private Date creatTime;
    private Date updateTime;
    private Date startTime;
    private Date endTime;
    private String duration;
    private int status;
}

package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PlanListItemDTO {
    private int id;
    private String name;
    private int faultNum;
    private String creator;
    private Date createTime;
    private Date updateTime;
    private int status;
}

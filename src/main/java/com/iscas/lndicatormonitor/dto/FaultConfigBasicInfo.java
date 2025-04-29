package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FaultConfigBasicInfo {
    private int id;
    private String name;
    private int creatorId;
    private Date createTime;
    private Date updateTime;
    private String graph;
    private String faultTypeConfig;
    /**
     * go端节点name
     * */
    private String nodeTag;
}

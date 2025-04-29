package com.iscas.lndicatormonitor.dto.planv2.detail;

import lombok.Data;

import java.util.List;

@Data
public class BasicInfo {
    private String name;
    private String applicationId;
    private Integer planType;
    private String excuteTime;
    private String sceneDesc;
    private String expection;
    private String loadId;
    private String graph;
    private String duration;
    private String workflowScript;
    private List<String> steadyIdList;
}

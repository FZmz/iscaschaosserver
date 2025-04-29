package com.iscas.lndicatormonitor.domain;

import lombok.Data;

@Data
public class Faultcorrelation {
    private Integer id;

    /**
    * 演练计划id
    */
    private Integer planId;

    /**
    * 故障演练id
    */
    private Integer faultConfigId;
}
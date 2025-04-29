package com.iscas.lndicatormonitor.domain;

import lombok.Data;

@Data
public class Planduration {
    private Integer id;

    /**
    * 持续时间
    */
    private String duration;

    /**
    * 计划id
    */
    private Integer planId;
}
package com.iscas.lndicatormonitor.domain;

import lombok.Data;

@Data
public class ObservedIndex {
    /**
    * 系统指标id
    */
    private Integer id;

    /**
    * 指标名
    */
    private String name;

    /**
    * 观测指标类型(1:系统指标 2:压测指标)
    */
    private Integer type;

    /**
    * 所属故障演练id
    */
    private Integer faultConfigId;
}
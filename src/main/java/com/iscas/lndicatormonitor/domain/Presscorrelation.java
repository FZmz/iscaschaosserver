package com.iscas.lndicatormonitor.domain;

import lombok.Data;

@Data
public class Presscorrelation {
    private Integer id;

    /**
    * k6压测结果id
    */
    private Integer k6Id;

    /**
    * 记录id
    */
    private Integer recordId;
}
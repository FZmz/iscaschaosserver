package com.iscas.lndicatormonitor.domain;

import lombok.Data;

@Data
public class Address {
    private Integer id;

    /**
    * 物理机故障chaosd地址
    */
    private String address;

    /**
    * 故障配置id
    */
    private Integer faultConfigId;
}
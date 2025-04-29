package com.iscas.lndicatormonitor.domain;

import lombok.Data;

@Data
public class Selector {
    /**
    * selectorid
    */
    private Integer id;

    private Integer faultConfigId;

    /**
    * 命名空间
    */
    private String namespace;

    /**
    * pod label
    */
    private String labels;

    /**
    * pod 名称
    */
    private String podnames;
}
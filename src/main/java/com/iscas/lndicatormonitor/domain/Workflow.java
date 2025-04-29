package com.iscas.lndicatormonitor.domain;

import lombok.Data;

@Data
public class Workflow {
    /**
     * 工作流id
     */
    private Integer id;

    /**
     * 内容
     */
    private String content;

    /**
     * 工作流内部节点名
     */
    private String nodes;

}
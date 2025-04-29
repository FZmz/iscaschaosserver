package com.iscas.lndicatormonitor.dto;

import lombok.Data;

@Data
public class WorkflowDTO {
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

package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PlanDTO {
    /**
     * 计划id
     */
    private Integer id;

    /**
     * 计划名称
     */
    private String name;

    /**
     * 计划创建时间
     */
    private Date createTime;

    /**
     * 计划更新时间
     */
    private Date updateTime;

    /**
     * 场景描述
     */
    private String sceneDesc;

    /**
     * 计划预期
     */
    private String expection;

    /**
     * 流程图id
     */
    private Integer workflowId;

    /**
     * 创建人id
     */
    private Integer creatorId;

    /**
     * 压测文件内容
     */
    private String pressContent;

    /**
     * 周期执行
     */
    private String schedule;

    /**
     * 流程图数据
     */
    private String graph;

    /**
     * 计划开始时间
     */
    private Date startTime;

    /**
     * 计划结束时间
     */
    private Date endTime;

    private String duration;
}

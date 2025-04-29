package com.iscas.lndicatormonitor.dto.planv2;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Planv2DTO {
    /**
     * 计划id
     */
    private Integer id;

    /**
     * 计划名称
     */
    private String name;


    /*
    * 应用ID
    * */
    private String applicationId;
    /**
     * 计划创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 计划更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
     * 流程图数据
     */
    private String graph;

    private String duration;

    /**
     * 计划类型 -- add
     */
    private int planType; // 1: 立即执行 2: 定点执行

    /**
     * 执行时间 -- add
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date excuteTime;

    /**
     * 压测id -- add
     */
    private String loadId;
}
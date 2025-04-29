package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 计划v2表
 * @TableName planv2
 */
@TableName(value ="planv2")
@Data
public class Planv2 implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
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
    * 应用Id
    */
    private String applicationId;

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
     * workflowId
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

    /**
     * 计划类型 1:立即执行 2:定点执行
     */
    private Integer planType;

    /**
     * 执行时间
     */
    private Date excuteTime;

    /**
     * 压测id
     */
    private String loadId;

    /**
     * 执行时间
     */
    private String duration;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
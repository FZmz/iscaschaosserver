package com.iscas.lndicatormonitor.domain.recordv2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 混沌工程演练记录表
 * @TableName chaos_exercise_records
 */
@TableName(value ="chaos_exercise_records")
@Data
public class ChaosExerciseRecords implements Serializable {
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdTime;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 执行状态(0: 未运行, 1: 运行中, 2: 成功, 3: 失败)
     */
    @TableField(value = "record_status")
    private Integer recordStatus;

    /**
     * 演练人名称
     */
    @TableField(value = "player_name")
    private String playerName;

    /**
     * 演练计划ID
     */
    @TableField(value = "plan_id")
    private Integer planId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
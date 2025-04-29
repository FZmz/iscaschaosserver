package com.iscas.lndicatormonitor.domain.scenario;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 故障场景表
 * @TableName fault_scenario
 */
@TableName(value ="fault_scenario")
@Data
public class FaultScenario implements Serializable {
    /**
     * 故障场景ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的演练计划ID
     */
    @TableField(value = "plan_id")
    private Integer planId;

    /**
     * 场景分类ID
     */
    @TableField(value = "category_id")
    private Integer categoryId;

    /**
     * 场景标签ID
     */
    @TableField(exist = false)
    private List<Integer> tagIds;

    /**
     * 场景名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 场景描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 创建者名称
     */
    @TableField(value = "creator_name")
    private String creatorName;

    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
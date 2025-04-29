package com.iscas.lndicatormonitor.domain.scenario;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 场景分类表
 * @TableName scenario_category
 */
@TableName(value ="scenario_category")
@Data
public class ScenarioCategory implements Serializable {
    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类名称
     */
    @TableField(value = "category_name")
    private String categoryName;

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
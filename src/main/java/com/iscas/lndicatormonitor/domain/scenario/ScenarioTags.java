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
 * 场景标签表
 * @TableName scenario_tags
 */
@TableName(value ="scenario_tags")
@Data
public class ScenarioTags implements Serializable {
    /**
     * 标签ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标签名称
     */
    @TableField(value = "tags_name")
    private String tagsName;

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
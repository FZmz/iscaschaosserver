package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName load
 */
@TableName(value = "`load`")
@Data
public class Load implements Serializable {
    /**
     * 负载id
     */
    @TableId
    private String id;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 负载名称
     */
    private String name;

    /**
     * 负载类型(0: script 1: api)
     */
    private Integer type;

    /**
     * 创建时间
     */
 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creatTime;

    /**
     * 更新时间
     */
 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
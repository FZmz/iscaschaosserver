package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName steadystate
 */
@TableName(value ="steadystate")
@Data
public class Steadystate implements Serializable {
    /**
     * 稳态定义id
     */
    @TableId
    private String id;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 稳态id
     */
    private Integer steadyStateId;

    /**
     * 稳态定义名称
     */
    private String name;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 目标id服务
     */
    private String targetService;

    /**
     * 值
     */
    private Integer value;

    /**
     * 比较符号
     */
    private String comparator;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
package com.iscas.lndicatormonitor.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
     * 稳态id
     */
    @TableId
    private String id;

    /**
     * 应用id
     */
    private String application_id;

    /**
     * 稳态名称
     */
    private String name;

    /**
     * 稳态类型(0: 全局稳态 1: 局部稳态)
     */
    private Integer type;

    /**
     * 指标
     */
    private String indicator;

    /**
     * 比较符号
     */
    private String comparator;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 是否删除
     */
    private Integer is_delete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
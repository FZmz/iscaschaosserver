package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName stateBound
 */
@TableName(value ="stateBound")
@Data
public class Statebound implements Serializable {
    /**
     * 稳态绑定id
     */
    @TableId
    private String id;

    /**
     * 绑定类型（workflow、faultConfig）
     */
    private Integer boundType;

    /**
     * 绑定id
     */
    @TableField("bound_id")
    private String boundId;

    @TableField("steady_id")
    private String steadyId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
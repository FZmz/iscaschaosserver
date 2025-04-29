package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName steady_state_repository
 */
@TableName(value ="steady_state_repository")
@Data
public class SteadyStateRepository implements Serializable {
    /**
     * 稳态指标id
     */
    @TableId
    private Integer id;

    /**
     * 稳态名
     */
    private String steadyStateName;

    /**
     * 处理器
     */
    private String handler;

    /**
     * 稳态类型
     */
    private Integer type;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
    /**
     * 稳态单位
     */
    private String steadyStateUnit;

    /**
     * 处理路径
     */
    private String handlePath;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
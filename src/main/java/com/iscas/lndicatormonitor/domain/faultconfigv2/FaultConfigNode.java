package com.iscas.lndicatormonitor.domain.faultconfigv2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName fault_config_node
 */
@TableName(value ="fault_config_node")
@Data
public class FaultConfigNode implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer faultConfigId;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private Integer nodeIndex;

    /**
     * 
     */
    private Integer nodeStatus;

    /**
     * 
     */
    private String nodeType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
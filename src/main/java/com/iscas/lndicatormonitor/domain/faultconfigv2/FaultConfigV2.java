package com.iscas.lndicatormonitor.domain.faultconfigv2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName fault_config_v2
 */
@TableName(value ="fault_config_v2")
@Data
public class FaultConfigV2 implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String creatorName;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 
     */
    private String graph;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private String nodeTag;

    /**
     * 
     */
    private int targetType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
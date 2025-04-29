package com.iscas.lndicatormonitor.domain.faultconfigv2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName obeservedIndexv2
 */
@TableName(value ="obeservedIndexv2")
@Data
public class Obeservedindexv2 implements Serializable {
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
    private Integer metricId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
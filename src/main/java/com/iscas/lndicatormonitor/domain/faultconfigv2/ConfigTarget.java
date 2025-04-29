package com.iscas.lndicatormonitor.domain.faultconfigv2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName config_target
 */
@TableName(value ="config_target")
@Data
public class ConfigTarget implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField("fault_config_id")  // 添加这个注解指定数据库字段名
    private Integer faultConfigId;

    /**
     * 
     */
    private String content;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
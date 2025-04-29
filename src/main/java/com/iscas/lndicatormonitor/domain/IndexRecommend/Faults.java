package com.iscas.lndicatormonitor.domain.IndexRecommend;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName faults
 */
@TableName(value ="faults")
@Data
public class Faults implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String faultName;

    /**
     * 
     */
    private String platform;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
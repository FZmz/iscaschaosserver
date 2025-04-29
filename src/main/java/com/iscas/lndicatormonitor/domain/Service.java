package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName service
 */
@TableName(value ="service")
@Data
public class Service implements Serializable {
    /**
     * 服务id
     */
    @TableId
    private String id;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 服务名称
     */
    private String name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
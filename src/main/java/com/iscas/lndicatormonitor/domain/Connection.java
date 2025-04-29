package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName connection
 */
@TableName(value ="connection")
@Data
public class Connection implements Serializable {
    /**
     * 连接id
     */
    private String id;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 来源id(服务)
     */
    private String fromId;

    /**
     * 去向id(服务)
     */
    private String toId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
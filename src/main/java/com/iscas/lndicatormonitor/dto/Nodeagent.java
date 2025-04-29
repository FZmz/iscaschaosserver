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
 * @TableName nodeagent
 */
@TableName(value ="nodeagent")
@Data
public class Nodeagent implements Serializable {
    /**
     * 代理id
     */
    @TableId
    private Integer id;

    /**
     * 应用id
     */
    private Integer application_id;

    /**
     * 代理名称
     */
    private String agent_name;

    /**
     * 代理ip
     */
    private String agent_ip;

    /**
     * 代理用户名
     */
    private String agent_usr;

    /**
     * 代理密码
     */
    private String agent_pwd;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 是否删除
     */
    private Integer is_delete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
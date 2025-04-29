package com.iscas.lndicatormonitor.domain;

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
    private String id;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 代理名称
     */
    private String agentName;

    /**
     * 代理ip
     */
    private String agentIp;

    /**
     * 代理用户名
     */
    private String agentUsr;

    /**
     * 代理密码
     */
    private String agentPwd;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * agent port
     */
    private int agentPort;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
package com.iscas.lndicatormonitor.dto.faultAgent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class NodeagentDTO {
    /**
     * 代理id
     */
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * agent port
     */
    private int agentPort;

    private int agentStatus;
}
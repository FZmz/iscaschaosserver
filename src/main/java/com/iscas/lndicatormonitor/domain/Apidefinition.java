package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName apiDefinition
 */
@TableName(value ="apiDefinition")
@Data
public class Apidefinition implements Serializable {
    /**
     * api Id
     */
    @TableId
    private String id;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * api名称
     */
    private String name;

    /**
     * api描述
     */
    private String description;

    /**
     * api方法
     */
    private String method;

    /**
     * api url
     */
    private String url;

    /**
     * 请求体
     */
    private String requestBody;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeaders;

    /**
     * 创建时间
     */
 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
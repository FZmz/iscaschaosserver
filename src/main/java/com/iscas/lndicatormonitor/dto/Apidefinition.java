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
    private String application_id;

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
    private String request_body;

    /**
     * 请求参数
     */
    private String request_params;

    /**
     * 请求头
     */
    private String request_headers;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 是否删除
     */
    private Integer is_delete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
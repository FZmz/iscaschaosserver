package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName load_api
 */
@TableName(value ="load_api")
@Data
public class LoadApi implements Serializable {
    /**
     * 负载API id
     */
    @TableId
    private String id;

    /**
     * 负载id
     */
    private String loadId;

    /**
     * 目标服务
     */
    private String targetService;

    /**
     * 用户数量
     */
    private Integer userCount;

    /**
     * 负载模式(0:constant,1: ramp-up)
     */
    private String loadPattern;

    /**
     * 持续时间
     */
    private String duration;

    /**
     * 自定义url
     */
    private String requestUrl;

    /**
     * 自定义参数
     */
    private String requestParams;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * http方法
     */
    private String httpMethod;

    /**
     * 请求头
     */
    private String customHeaders;

    /**
     * 请求体
     */
    private String requestBody;

    /**
     * 爬升时间
     */
    private Integer rampUpTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
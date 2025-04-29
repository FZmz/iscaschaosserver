package com.iscas.lndicatormonitor.dto.faultConfigv2;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class FaultConfigv2QueryCriteria {
    /**
     * 故障配置名称
     */
    private String name;

    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 目标类型（0：k8s故障，1：物理机故障）
     */
    private Integer targetType;

    /**
     * 时间排序：ASC 或 DESC
     */
    private String orderByTime;

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
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;
}
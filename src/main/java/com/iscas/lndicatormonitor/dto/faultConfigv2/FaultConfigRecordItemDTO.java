package com.iscas.lndicatormonitor.dto.faultConfigv2;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class FaultConfigRecordItemDTO {

    private Integer id;
    /**
     * 配置名称
     */
    private String name;

    /**
     * 目标类型（0：k8s故障，1：物理机故障）
     */
    private Integer targetType;

    /**
     * 创建者名称
     */
    private String creatorName;

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
     * 关联稳态数量
     */
    private Integer steadyCount;

    /**
     * 观测指标数量
     */
    private Integer observedIndexCount;
}
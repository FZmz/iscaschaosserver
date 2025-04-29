package com.iscas.lndicatormonitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 审计日志查询条件
 */
@Data
public class AuditQueryCriteria {
    /**
     * 当前页码
     */
    private Integer page = 1;
    
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
    
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;
    
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 操作类型
     */
    private String operationType;
    
    /**
     * 操作名称
     */
    private String operationName;
} 
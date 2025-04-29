package com.iscas.lndicatormonitor.dto.recordv2;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Recordv2QueryCriteria {
    private String name;  // 计划名称
    private String recordStatus;  // 记录状态
    private String creator;  // 创建者
    private String orderByTime;  // 排序方式
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;  // 开始时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;  // 结束时间
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdTime;  // 创建时间（精确到天）
    
    private Integer pageNum = 1;  // 当前页码
    private Integer pageSize = 10;  // 每页大小
}
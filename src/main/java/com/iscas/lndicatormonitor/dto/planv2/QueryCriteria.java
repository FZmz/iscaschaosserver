package com.iscas.lndicatormonitor.dto.planv2;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class QueryCriteria {
    // 查询字段
    private String name;

    private String creator;

    private String planType;

    // 时间排序：ASC 或 DESC
    private String orderByTime;

    // 时间区间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    // 分页参数
    private Integer pageNum = 1;  // 默认第一页
    private Integer pageSize = 10;  // 默认每页10条
}
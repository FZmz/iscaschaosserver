package com.iscas.lndicatormonitor.dto.reportv2;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
public class ReportQueryCriteria {
    private String name;
    private String creator;
    private String status;
    private Integer recordId;
    private String orderByTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
    private Date endTime;
    
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 
package com.iscas.lndicatormonitor.dto.recordv2;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class RecordListDTO {
    private Integer id;
    private String planName;
    private Integer recordStatus;
    private String creatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private int isHaveLoad; // 0 无压测 1 有压测    
}
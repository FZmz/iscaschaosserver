package com.iscas.lndicatormonitor.dto.recordv2;

import java.util.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ExerciseLogDTO {
    private String message;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Timestamp timestamp;
    
    private String level;  // INFO, WARN, ERROR 等
    
    private String source; // 日志来源，比如 "WORKFLOW", "LOAD_TEST" 等
    
    public static ExerciseLogDTO create(String message, String level, String source) {
        ExerciseLogDTO log = new ExerciseLogDTO();
        log.setMessage(message);
        log.setTimestamp(new Timestamp(new Date().getTime()));
        log.setLevel(level);
        log.setSource(source);
        return log;
    }
    
}
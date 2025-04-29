package com.iscas.lndicatormonitor.domain.recordv2;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("exercise_log")
public class ExerciseLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long recordId;
    
    private String message;
    
    private Timestamp timestamp;
    
    private String level;  // INFO, WARN, ERROR
    
    private String source; // WORKFLOW, LOAD_TEST, PLAN, INJECTION, RECORD
}
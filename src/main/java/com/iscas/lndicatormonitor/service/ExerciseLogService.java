package com.iscas.lndicatormonitor.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.iscas.lndicatormonitor.dto.recordv2.ExerciseLogDTO;

public interface ExerciseLogService {
    SseEmitter subscribe(String recordId);
    void publishLog(String recordId, ExerciseLogDTO log);
    void complete(String recordId);

    // 新增方法
    List<ExerciseLogDTO> getLogsByRecordId(String recordId);
    void saveLog(String recordId, String message, String level, String source);
    void saveLogWithTimestamp(String recordId, String message, String level, String source, Timestamp timestamp);

}
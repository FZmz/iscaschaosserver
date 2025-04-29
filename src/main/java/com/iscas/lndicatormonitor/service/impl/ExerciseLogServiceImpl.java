package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iscas.lndicatormonitor.domain.recordv2.ExerciseLog;
import com.iscas.lndicatormonitor.dto.recordv2.ExerciseLogDTO;
import com.iscas.lndicatormonitor.mapper.ExerciseLogMapper;
import com.iscas.lndicatormonitor.service.ExerciseLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExerciseLogServiceImpl implements ExerciseLogService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    @Autowired
    private ExerciseLogMapper exerciseLogMapper;
    
    // 添加时间格式化器
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public SseEmitter subscribe(String recordId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(recordId, emitter);
        
        emitter.onCompletion(() -> emitters.remove(recordId));
        emitter.onTimeout(() -> emitters.remove(recordId));
        emitter.onError(e -> emitters.remove(recordId));
        
        try {
            // 发送历史日志
            List<ExerciseLogDTO> historyLogs = getLogsByRecordId(recordId);
            log.info("获取到历史日志数量: {}", historyLogs.size());
            
            for (ExerciseLogDTO logDTO : historyLogs) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("LOG")
                            .data(logDTO));
                    log.debug("发送历史日志: {}", logDTO.getMessage());
                } catch (IOException e) {
                    log.error("发送历史日志失败: {}", e.getMessage());
                    throw e;
                }
            }
            
            // 发送连接成功消息
            emitter.send(SseEmitter.event()
                    .name("CONNECT")
                    .data("Connected successfully"));
            log.info("发送连接成功消息");
            
        } catch (IOException e) {
            log.error("Error in subscribe method: {}", e.getMessage());
            emitters.remove(recordId);
            emitter.complete();
        }
        
        return emitter;
    }

    @Override
    public void publishLog(String recordId, ExerciseLogDTO logDTO) {
        try {
            // 1. 保存到数据库
            ExerciseLog exerciseLog = new ExerciseLog();
            exerciseLog.setRecordId(Long.parseLong(recordId));
            exerciseLog.setMessage(logDTO.getMessage());
            exerciseLog.setTimestamp(logDTO.getTimestamp());
            exerciseLog.setLevel(logDTO.getLevel());
            exerciseLog.setSource(logDTO.getSource());
            exerciseLogMapper.insert(exerciseLog);
            
            // 2. 推送日志
            SseEmitter emitter = emitters.get(recordId);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("LOG")
                            .data(logDTO));
                } catch (IOException e) {
                    log.error("发送日志消息失败: {}", e.getMessage());
                    emitters.remove(recordId);
                    emitter.complete();
                }
            }
        } catch (Exception e) {
            log.error("发布日志失败: {}", e.getMessage());
            throw new RuntimeException("发布日志失败: " + e.getMessage());
        }
    }

    @Override
    public void saveLog(String recordId, String message, String level, String source) {
        try {
            // 使用传入的时间戳或当前时间
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            
            ExerciseLogDTO logDTO = new ExerciseLogDTO();
            logDTO.setMessage(message);
            logDTO.setLevel(level);
            logDTO.setSource(source);
            logDTO.setTimestamp(currentTimestamp);
            
            publishLog(recordId, logDTO);
        } catch (Exception e) {
            log.error("保存日志失败: {}", e.getMessage());
            throw new RuntimeException("保存日志失败: " + e.getMessage());
        }
    }

    // 添加一个新方法，允许指定时间戳
    @Override
    public void saveLogWithTimestamp(String recordId, String message, String level, String source, Timestamp timestamp) {
        try {
            ExerciseLogDTO logDTO = new ExerciseLogDTO();
            logDTO.setMessage(message);
            logDTO.setLevel(level);
            logDTO.setSource(source);
            logDTO.setTimestamp(timestamp);
            
            publishLog(recordId, logDTO);
        } catch (Exception e) {
            log.error("保存日志失败: {}", e.getMessage());
            throw new RuntimeException("保存日志失败: " + e.getMessage());
        }
    }

    @Override
    public List<ExerciseLogDTO> getLogsByRecordId(String recordId) {
        try {
            LambdaQueryWrapper<ExerciseLog> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExerciseLog::getRecordId, Long.parseLong(recordId))
                    .orderByAsc(ExerciseLog::getTimestamp);
            
            List<ExerciseLog> logs = exerciseLogMapper.selectList(wrapper);
            log.debug("查询到 {} 条历史日志", logs.size());  // 添加日志
            
            return logs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取历史日志失败: {}", e.getMessage());
            throw e;
        }
    }

    private ExerciseLogDTO convertToDTO(ExerciseLog log) {
        ExerciseLogDTO dto = new ExerciseLogDTO();
        dto.setMessage(log.getMessage());
        dto.setLevel(log.getLevel());
        dto.setSource(log.getSource());
        dto.setTimestamp(log.getTimestamp());  // Timestamp 类型直接传递
        return dto;
    }

    @Override
    public void complete(String recordId) {
        SseEmitter emitter = emitters.get(recordId);
        if (emitter != null) {
            emitter.complete();
            emitters.remove(recordId);
        }
    }
}
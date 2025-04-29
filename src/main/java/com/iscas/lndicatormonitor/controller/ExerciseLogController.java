package com.iscas.lndicatormonitor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.dto.recordv2.ExerciseLogDTO;
import com.iscas.lndicatormonitor.service.ExerciseLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/logs")
public class ExerciseLogController {
    
    @Autowired
    private ExerciseLogService exerciseLogService;
    
    @GetMapping("/subscribe")
    @OperationLog("订阅日志")
    public SseEmitter subscribe(@RequestParam String recordId) {
        return exerciseLogService.subscribe(recordId);
    }
    @GetMapping("/history")
    @OperationLog("获取历史日志")
    public CustomResult getHistoryLogs(@RequestParam String recordId) {
        List<ExerciseLogDTO> logs = exerciseLogService.getLogsByRecordId(recordId);
        return CustomResult.ok(logs);
    }
}
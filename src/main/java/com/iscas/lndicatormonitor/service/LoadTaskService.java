package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.LoadTask;

import java.util.Map;

/**
* @author mj
* @description 针对表【load_task】的数据库操作Service
* @createDate 2025-02-17 22:12:31
*/
public interface LoadTaskService extends IService<LoadTask> {
    /**
     * 根据记录ID获取性能指标数据
     * @param recordId 记录ID
     * @return 性能指标数据
     */
    Map<String, Object> getPerformanceMetricsByRecordId(String recordId);
}

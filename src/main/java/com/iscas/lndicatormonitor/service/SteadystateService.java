package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Steadystate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author mj
* @description 针对表【steadystate】的数据库操作Service
* @createDate 2025-01-13 13:52:34
*/
public interface SteadystateService extends IService<Steadystate> {

    /**
     * 根据计划ID获取稳态指标
     *
     * @param planId 计划ID
     * @param from   起始时间
     * @param to     结束时间
     * @return 稳态指标列表
     */
    List<Map<String, Object>> getMetricsByPlanId(String planId, Long from, Long to);

    /**
     * 根据故障配置ID获取稳态指标
     *
     * @param faultConfigId 故障配置ID
     * @param from          起始时间
     * @param to            结束时间
     * @return 稳态指标列表
     */
    List<Map<String, Object>> getMetricsByFaultConfigId(String faultConfigId, Long from, Long to);
    
    /**
     * 保存稳态数据
     *
     * @param entity 稳态实体
     * @return 自定义结果
     */
    CustomResult saveSteadystate(Steadystate entity);
}
package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Planv2;
import com.iscas.lndicatormonitor.dto.planv2.Planv2DTO;
import com.iscas.lndicatormonitor.dto.planv2.QueryCriteria;
import com.iscas.lndicatormonitor.dto.planv2.update.UpdatePlanDTO;
import com.iscas.lndicatormonitor.dto.planv2.TargetObjectNumDTO;

/**
* @author mj
* @description 针对表【planv2(计划v2表)】的数据库操作Service
* @createDate 2025-01-14 20:47:46
*/
public interface Planv2Service extends IService<Planv2> {

    /**
     * 插入计划
     * @param planv2DTO 计划DTO
     * @param faultConfigIdList 故障配置ID列表
     * @return CustomResult
     */
    CustomResult planInsert(Planv2DTO planv2DTO, int[] faultConfigIdList);

    CustomResult queryPlanList(QueryCriteria criteria);

    CustomResult getPlanDetail(Integer planId);
    /**
     * 更新计划
     * @param updateDTO 更新参数
     * @return CustomResult
     */
    CustomResult updatePlan(UpdatePlanDTO updateDTO);



    CustomResult getPlanUpdateInfo(String planId);

    CustomResult deletePlan(Integer planId);
    
    /**
     * 执行计划
     * @param planv2 计划对象
     * @param playerName 执行者名称
     * @return 执行结果
     */
    CustomResult executePlan(Planv2 planv2, String playerName);
    
    /**
     * 根据ID执行计划
     * @param planId 计划ID
     * @return 执行结果
     */
    CustomResult executePlanById(Integer planId);

    /**
     * 获取计划故障注入对象数量
     * @param planId 计划ID
     * @return TargetObjectNumDTO
     */
    TargetObjectNumDTO getFaultInjectObjectNum(Integer planId);
}
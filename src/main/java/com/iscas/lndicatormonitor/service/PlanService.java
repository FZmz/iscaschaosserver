package com.iscas.lndicatormonitor.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.Plan;
import com.iscas.lndicatormonitor.dto.PlanDTO;

import java.util.List;

public interface PlanService  extends IService<Plan> {
    Boolean planInsert(PlanDTO planDTO, int[] faultConfigIdList) throws Exception;

    List<Plan> getAllPlan() throws Exception;

    Plan getPlanById(int planId) throws Exception;

    String getPlanNameById(int planId) throws Exception;
    Boolean planUpdate(PlanDTO planDTO) throws Exception;

    int getPlanSumNum()throws Exception;

    int deleteByPrimaryKey(Integer id);
}

package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.domain.*;
import com.iscas.lndicatormonitor.dto.PlanDTO;
import com.iscas.lndicatormonitor.mapper.PlanMapper;
import com.iscas.lndicatormonitor.mapper.PlancollectionMapper;
import com.iscas.lndicatormonitor.service.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan>
        implements PlanService {
    @Autowired
    PlanMapper planMapper;

    @Autowired
    FaultcorrelationService faultcorrelationService;

    @Autowired
    PlandurationService plandurationService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    PlancollectionMapper plancollectionMapper;
    @Override
    public Boolean planInsert(PlanDTO planDTO, int[] faultConfigIdList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (planDTO == null) {
            return false;
        }
        Plan plan = new Plan();
        BeanUtils.copyProperties(planDTO, plan);
        plan.setPressContent(planDTO.getPressContent());
        int affectedRows = planMapper.insert(plan);
        if (affectedRows>0){
            String planAsString = objectMapper.writeValueAsString(plan.getId());
            rabbitTemplate.convertAndSend("", "plan", planAsString);
            for (int faultConfigId : faultConfigIdList){
                Faultcorrelation faultcorrelation = new Faultcorrelation();
                faultcorrelation.setPlanId(plan.getId());
                faultcorrelation.setFaultConfigId(faultConfigId);
                faultcorrelationService.insert(faultcorrelation);
            }
            Planduration planduration = new Planduration();
            planduration.setDuration(planDTO.getDuration());
            planduration.setPlanId(plan.getId());
            plandurationService.insert(planduration);
            Plancollection plancollection = new Plancollection();
            plancollection.setPlanId(plan.getId());
            plancollectionMapper.insert(plancollection);
        }
        // 日志记录
        // log.info("Inserted plan with ID: " + plan.getId());
        return affectedRows > 0;
    }

    @Override
    public List<Plan> getAllPlan() throws Exception {
        List<Plan> planList= planMapper.selectAll();
        return planList;
    }

    @Override
    public Plan getPlanById(int planId) throws Exception {
        return planMapper.selectByPrimaryKey(planId);
    }

    @Override
    public String getPlanNameById(int planId) throws Exception {
        return planMapper.getPlanNameById(planId);
    }

    @Override
    public Boolean planUpdate(PlanDTO planDTO) throws Exception {
        Plan plan = new Plan();
        BeanUtils.copyProperties(planDTO, plan);
        // 获取对应的planDuration
        Planduration planduration = plandurationService.selectByPlanId(planDTO.getId());
        planduration.setDuration(planDTO.getDuration());
        plandurationService.updateByPrimaryKey(planduration);
        int affectedRows = planMapper.updateByPrimaryKey(plan);
        return affectedRows > 0;
    }

    @Override
    public int getPlanSumNum() throws Exception {
        return planMapper.getPlanSumNum();
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return planMapper.deleteByPrimaryKey(id);
    }
}

package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Planv2;
import com.iscas.lndicatormonitor.domain.Statebound;
import com.iscas.lndicatormonitor.domain.recordv2.ChaosExerciseRecords;
import com.iscas.lndicatormonitor.dto.planv2.AddPlanv2DTO;
import com.iscas.lndicatormonitor.dto.planv2.Planv2DTO;
import com.iscas.lndicatormonitor.dto.planv2.QueryCriteria;
import com.iscas.lndicatormonitor.dto.planv2.update.UpdatePlanDTO;
import com.iscas.lndicatormonitor.dto.workflow.WorkflowSummary;
import com.iscas.lndicatormonitor.service.ChaosExerciseRecordsService;
import com.iscas.lndicatormonitor.service.Planv2Service;
import com.iscas.lndicatormonitor.service.StateboundService;
import com.iscas.lndicatormonitor.service.WorkflowService;
import com.iscas.lndicatormonitor.utils.WorkflowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/planv2")
@Slf4j
public class Planv2Controller {
    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private StateboundService stateboundService;

    @Autowired
    private Planv2Service planv2Service;

    @Autowired
    private WorkflowUtils workflowUtils;

    @Autowired
    private ChaosExerciseRecordsService chaosExerciseRecordsService;

    @PostMapping("/addPlan")
    @OperationLog("新增计划")
    @Transactional(rollbackFor = Exception.class)
    public CustomResult insertPlanv2(@RequestBody AddPlanv2DTO addPlanv2DTO) throws Exception {
        try {
            // 1. 手动生成planId
            Integer planId = Math.abs(UUID.randomUUID().hashCode());    // 使用ID生成工具类

            // 2. 插入 workflow
            int workflowId = workflowService.workflowInsert(addPlanv2DTO.getWorkflowDTO());

            // 3. 处理稳态关联
            if (addPlanv2DTO.getSteadyIdList() != null && !addPlanv2DTO.getSteadyIdList().isEmpty()) {
                for (String steadyId : addPlanv2DTO.getSteadyIdList()) {
                    Statebound statebound = new Statebound();
                    statebound.setBoundType(0);
                    statebound.setBoundId(String.valueOf(planId));  // 使用生成的planId
                    statebound.setSteadyId(steadyId);
                    stateboundService.save(statebound);
                }
            }

            // 4. 插入plan
            Planv2DTO planv2DTO = addPlanv2DTO.getPlanv2DTO();
            planv2DTO.setId(planId);  // 设置生成的id
            planv2DTO.setWorkflowId(workflowId);
            CustomResult result = planv2Service.planInsert(planv2DTO, addPlanv2DTO.getFaultConfigIdList());

            if (result.getStatus() == 20000) {  // 成功状态码为20000
                return CustomResult.ok(planId);  // 返回创建的planId
            } else {
                return CustomResult.fail(result.getMsg());  // 返回服务层的错误信息
            }

        } catch (Exception e) {
            log.error("新增计划失败", e);
            return CustomResult.fail("新增计划失败 ");
        }
    }

    @PostMapping("/list")
    @OperationLog("获取计划列表")
    public CustomResult getPlanList(@RequestBody QueryCriteria criteria) {
        return planv2Service.queryPlanList(criteria);
    }

    @GetMapping("/detail")
    @OperationLog("获取计划详情")
    public CustomResult getPlanDetail(@RequestParam Integer planId) {
        return planv2Service.getPlanDetail(planId);
    }

    @GetMapping("/update-info")
    @OperationLog("获取计划更新信息")
    public CustomResult getPlanUpdateInfo(@RequestParam  String planId) {
        return planv2Service.getPlanUpdateInfo(planId);
    }

    @PutMapping("/update")
    @OperationLog("更新计划")
    public CustomResult updatePlan(@RequestBody UpdatePlanDTO updateDTO) {
        try {
            // 1. 检查计划是否存在
            Planv2 plan = planv2Service.getById(updateDTO.getPlanv2DTO().getId());
            if (plan == null) {
                return CustomResult.fail("计划不存在");
            }
            
            // 2. 检查是否有演练记录
            long recordCount = chaosExerciseRecordsService.lambdaQuery()
                .eq(ChaosExerciseRecords::getPlanId, updateDTO.getPlanv2DTO().getId())
                .count();
                
            if (recordCount > 0) {
                return CustomResult.fail("已有演练记录的计划不能进行更新");
            }
            
            // 3. 执行更新
            return planv2Service.updatePlan(updateDTO);
        } catch (Exception e) {
            log.error("更新计划失败", e);
            return CustomResult.fail("更新计划失败: " + null);
        }
    }

    @DeleteMapping("/delete")
    @OperationLog("删除计划")
    public CustomResult deletePlan(@RequestParam Integer planId) {
        return planv2Service.deletePlan(planId);
    }

    @PostMapping("/execute")
    @OperationLog("执行计划")
    public CustomResult executePlan(@RequestParam  Integer planV2Id,@RequestParam String  playerName) {
        Planv2 planv2 = planv2Service.getById(planV2Id);
        if (planv2 == null) {
            return CustomResult.fail("计划不存在");
        }

        if (planv2.getPlanType() == 2) {
            return CustomResult.fail("不支持定点执行");
        }

        return planv2Service.executePlan(planv2,playerName);
    }

    @PostMapping("/checkWorkflowStatus")
    @OperationLog("检查工作流状态")
    public CustomResult checkWorkflowStatus(@RequestBody WorkflowSummary workflowSummary) {
        log.info("接收到工作流状态检查请求，工作流名称：{}", workflowSummary.getWorkflowName());

        try {
            boolean isSuccessful = workflowUtils.isWorkflowSuccessful(workflowSummary);
            log.info("工作流状态检查完成，结果：{}", isSuccessful);
            return CustomResult.ok(isSuccessful);
        } catch (Exception e) {
            log.error("工作流状态检查出错", e);
            return CustomResult.fail("工作流状态检查失败：" + null);
        }
    }
}
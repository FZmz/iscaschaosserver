package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Plan;
import com.iscas.lndicatormonitor.domain.Record;
import com.iscas.lndicatormonitor.domain.Statebound;
import com.iscas.lndicatormonitor.dto.*;
import com.iscas.lndicatormonitor.service.*;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plan")
public class PlanController {
    @Autowired
    PlanService planService;
    @Autowired
    WorkflowService workflowService;

    @Autowired
    RecordService recordService;

    @Autowired
    UsersService usersService;

    @Autowired
    PlancollectionService plancollectionService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private  FaultcorrelationService faultcorrelationService;

    @Autowired
    private StateboundService stateboundService;

    private static final Logger logger = Logger.getLogger(PlanController.class);

    @PostMapping("/insertPlan")
    @OperationLog("插入计划")
    public CustomResult insertPlan(@RequestBody AddPlanDTO addPlanDTO) throws Exception {

        int workflowId = workflowService.workflowInsert(addPlanDTO.getWorkflowDTO());
        if (addPlanDTO.getSteadyIdList() != null && !addPlanDTO.getSteadyIdList().isEmpty()){
            for (String steadyId : addPlanDTO.getSteadyIdList()){
                Statebound statebound = new Statebound();
                // 稳态类型为全局型
                statebound.setBoundType(0);
                statebound.setBoundId(String.valueOf(workflowId));
                statebound.setSteadyId(steadyId);
                stateboundService.save(statebound);
            }

        }
        PlanDTO planDTO = addPlanDTO.getPlanDTO();
        planDTO.setWorkflowId(workflowId);
        Boolean result =  planService.planInsert(planDTO,addPlanDTO.getFaultConfigIdList());
        if (result){
            // 关联
            return CustomResult.ok();
        }else {
            return new CustomResult(40000,"插入失败",null);
        }

    }

    @GetMapping("/getAllPlan")
    @OperationLog("获取所有计划")
    public CustomResult getAllPlan() throws Exception {
        List<Plan> planList = planService.getAllPlan();
        List<PlanListItemDTO> planListItemDTOList = new ArrayList<>();

        for (Plan plan : planList) {
            PlanListItemDTO dto = new PlanListItemDTO();

            // 先取基本字段
            dto.setId(plan.getId());
            dto.setName(plan.getName());
            dto.setCreateTime(plan.getCreateTime());
            dto.setUpdateTime(plan.getUpdateTime());

            // 设置创建者名称
            dto.setCreator(usersService.getRealNameById(plan.getCreatorId()));

            // 计算注入故障总数
            String nodesString = workflowService.getWorkflowById(plan.getWorkflowId()).getNodes();
            if (nodesString != null && !nodesString.isEmpty()) {
                dto.setFaultNum(nodesString.split(",").length);
            } else {
                dto.setFaultNum(0);
            }
            // 计算状态
            List<Record> records = recordService.getRecordsByPlanId(plan.getId());
            Record latestRecord = null;

            for (Record record : records) {
                if (record.getEndTime() == null) {
                    latestRecord = record;
                    break;  // 当发现endTime为null的记录时，立即终止循环
                } else if (latestRecord == null || record.getEndTime().after(latestRecord.getEndTime())) {
                    latestRecord = record;
                }
            }
            if (latestRecord != null) {
                dto.setStatus(latestRecord.getRecordStatus() == null ? 0 : latestRecord.getRecordStatus());
            } else {
                dto.setStatus(0); // 未开始
            }
            planListItemDTOList.add(dto);
        }
        return CustomResult.ok(planListItemDTOList);
    }

    @GetMapping("/getPlanListByPage")
    @OperationLog("获取计划列表")
    public CustomResult getPlanListByPage(@RequestParam(value = "page", defaultValue = "1") int pagenum,@RequestParam(value = "size", defaultValue = "10") int size) throws Exception {

        // 初始化分页对象
        Page<Plan> page = new Page<>(pagenum, size);
        logger.debug("Initialized Page object with pageNumber=" + pagenum +
                ", pageSize=" + size);

        // 分页查询计划列表
        Page<Plan> planPage = planService.page(page);
        logger.debug("Queried planService.page, total records: " + planPage.getTotal());

        List<Plan> planList = planPage.getRecords();
        logger.info("Fetched " + planList.size() + " records for the current page.");

        // 转换为 DTO
        List<PlanListItemDTO> planListItemDTOList = new ArrayList<>();
        for (Plan plan : planList) {
            logger.debug("Processing plan with ID: " + plan.getId());

            PlanListItemDTO dto = new PlanListItemDTO();

            // 基本字段
            dto.setId(plan.getId());
            dto.setName(plan.getName());
            dto.setCreateTime(plan.getCreateTime());
            dto.setUpdateTime(plan.getUpdateTime());

            // 创建者名称
            String creatorName = usersService.getRealNameById(plan.getCreatorId());
            dto.setCreator(creatorName);
            logger.debug("Fetched creator name: " + creatorName + " for plan ID: " + plan.getId());

            // 注入故障总数
            String nodesString = workflowService.getWorkflowById(plan.getWorkflowId()).getNodes();
            if (nodesString != null && !nodesString.isEmpty()) {
                dto.setFaultNum(nodesString.split(",").length);
            } else {
                dto.setFaultNum(0);
            }
            logger.debug("Calculated faultNum: " + dto.getFaultNum() + " for plan ID: " + plan.getId());

            // 计算状态
            List<Record> records = recordService.getRecordsByPlanId(plan.getId());
            logger.debug("Fetched " + records.size() + " records for plan ID: " + plan.getId());

            Record latestRecord = null;
            for (Record record : records) {
                if (record.getEndTime() == null) {
                    latestRecord = record;
                    break;
                } else if (latestRecord == null || record.getEndTime().after(latestRecord.getEndTime())) {
                    latestRecord = record;
                }
            }
            if (latestRecord != null) {
                dto.setStatus(latestRecord.getRecordStatus() == null ? 0 : latestRecord.getRecordStatus());
            } else {
                dto.setStatus(0);
            }
            logger.debug("Determined status: " + dto.getStatus() + " for plan ID: " + plan.getId());
            planListItemDTOList.add(dto);
        }

        // 封装分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", planPage.getTotal());
        result.put("data", planListItemDTOList);
        logger.info("Successfully prepared response with total records: " + planPage.getTotal());

        return CustomResult.ok(result);
    }

    // 获取单个计划信息 包括计划的基本信息(包括流程图、压测文件等) 还有执行记录信息
    @GetMapping("/getPlanById")
    @OperationLog("获取计划详情")
    public CustomResult getPlanById(int planId) throws Exception {
        // 获取单个计划信息
        Plan plan = planService.getPlanById(planId);
        PlanSpecDTO planSpecDTO = new PlanSpecDTO();
        BeanUtils.copyProperties(plan,planSpecDTO);
        planSpecDTO.setCreator(usersService.getRealNameById(plan.getCreatorId()));
        SinglePlanDTO singlePlanDTO = new SinglePlanDTO();
        singlePlanDTO.setPlanSpecDTO(planSpecDTO);
        // 获取此计划下的所有记录信息
        List<Record> recordList = recordService.getRecordsByPlanId(plan.getId());
        List<RecordItemDTO> recordItemDTOList = new ArrayList<>();
        for (Record record: recordList){
            RecordItemDTO recordItemDTO = new RecordItemDTO();
            BeanUtils.copyProperties(record,recordItemDTO);
            recordItemDTO.setPlayer(usersService.getRealNameById(record.getPlayerId()));
            recordItemDTOList.add(recordItemDTO);
        }
        List<String> stateIdList = stateboundService.getSteadyIdsByBoundId(String.valueOf(planId));
        if (stateIdList.size() > 0){
            singlePlanDTO.setSteadyList(stateIdList);
        }
        singlePlanDTO.setRecordList(recordItemDTOList);
        return CustomResult.ok(singlePlanDTO);
    }

    // 更新计划信息
    @PostMapping("/updatePlan")
    @OperationLog("更新计划")
    public CustomResult updatePlan(@RequestBody AddPlanDTO updatePlanDTO) throws Exception {
        WorkflowDTO workflowDTO = updatePlanDTO.getWorkflowDTO();
        workflowDTO.setId(updatePlanDTO.getPlanDTO().getWorkflowId());
        if (planService.planUpdate(updatePlanDTO.getPlanDTO()) && workflowService.workflowUpdate(workflowDTO)){
            return CustomResult.ok();
        }else {
            return new CustomResult(40000,"更新失败",null);
        }
    }

    @PostMapping("/delPlan")
    @OperationLog("删除计划")
    public CustomResult delPlan(int planId){

        if (recordService.checkPlanIdExists(planId) == 0){
            plancollectionService.deleteByPlanId(planId);
            faultcorrelationService.deleteByPlanId(planId);

            return CustomResult.ok(planService.deleteByPrimaryKey(planId));
        }
        else {
            return new  CustomResult(40000,"相关记录未删除",null);
        }

    }
}

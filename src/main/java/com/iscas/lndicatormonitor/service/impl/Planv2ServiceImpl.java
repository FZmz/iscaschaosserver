package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.*;
import com.iscas.lndicatormonitor.domain.recordv2.ChaosExerciseRecords;
import com.iscas.lndicatormonitor.dto.WorkflowDTO;
import com.iscas.lndicatormonitor.dto.planv2.AddPlanv2DTO;
import com.iscas.lndicatormonitor.dto.planv2.Planv2DTO;
import com.iscas.lndicatormonitor.dto.planv2.Planv2ListItemDTO;
import com.iscas.lndicatormonitor.dto.planv2.QueryCriteria;
import com.iscas.lndicatormonitor.dto.planv2.TargetObjectNumDTO;
import com.iscas.lndicatormonitor.dto.planv2.detail.*;
import com.iscas.lndicatormonitor.dto.planv2.update.UpdatePlanDTO;
import com.iscas.lndicatormonitor.dto.workflow.WorkflowSummary;
import com.iscas.lndicatormonitor.mapper.Planv2Mapper;
import com.iscas.lndicatormonitor.service.*;
import com.iscas.lndicatormonitor.utils.LoadTestUtils;
import com.iscas.lndicatormonitor.utils.WorkflowUtils;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Iterator;
import com.iscas.lndicatormonitor.domain.Workflow;
import com.iscas.lndicatormonitor.service.WorkflowService;

/**
 * @author mj
 * @description 针对表【planv2(计划v2表)】的数据库操作Service实现
 * @createDate 2025-01-14 20:47:46
 */
@Slf4j
@Service
public class Planv2ServiceImpl extends ServiceImpl<Planv2Mapper, Planv2>
        implements Planv2Service {
    @Autowired
    private Planv2Mapper planv2Mapper;

    @Autowired
    private FaultcorrelationService faultcorrelationService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private WorkflowcorrelationService correlationService;
    @Autowired
    private StateboundService stateboundService;

    @Autowired
    private LoadService loadService;

    @Autowired
    private LoadTestUtils loadTestUtils;
    @Autowired
    private WorkflowUtils workflowUtils;

    @Autowired
    private ChaosExerciseRecordsService chaosExerciseRecordsService;

    @Autowired
    private ConfigTargetService configTargetService;

    @Autowired
    private KubernetesClient kubernetesClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExerciseLogService exerciseLogService;

    @Autowired
    private LoadCorrelationService loadCorrelationService;

    @Value("${kubernetes.api-url}")
    private String k8sApiServerUrl;

    @Value("${kubernetes.token}")
    private String k8sToken;

    @Autowired
    private RestTemplate restTemplate;

    Timestamp startTimestamp = null;

    // 在执行过程中记录日志
    private void logExercise(String recordId, String message, String level, String source, Timestamp timestamp) {
        exerciseLogService.saveLogWithTimestamp(recordId, message, level, source, timestamp);
    }

    private void logExercise(String recordId, String message, String level, String source) {
        exerciseLogService.saveLog(recordId, message, level, source);
    }

    /**
     * 在压测步骤之间添加暂停时间
     * 
     * @param recordId     记录ID，用于日志记录
     * @param pauseSeconds 暂停的秒数
     */
    private void pauseBetweenLoadTests(String recordId, Long pauseSeconds) {
        try {
            logExercise(recordId, "暂停" + pauseSeconds + "秒，准备执行下一个压测步骤", "INFO", "LOAD_TEST");
            Thread.sleep(pauseSeconds);
            logExercise(recordId, "暂停结束，继续执行", "INFO", "LOAD_TEST");
        } catch (InterruptedException e) {
            log.error("暂停过程被中断", e);
            Thread.currentThread().interrupt();
            logExercise(recordId, "暂停过程被中断: " + e.getMessage(), "ERROR", "LOAD_TEST");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomResult planInsert(Planv2DTO planv2DTO, int[] faultConfigIdList) {
        try {
            // 1. 参数校验
            if (planv2DTO == null) {
                return CustomResult.fail("计划数据不能为空");
            }
            if (StringUtils.isEmpty(planv2DTO.getName())) {
                return CustomResult.fail("计划名称不能为空");
            }

            // 2. 检查名称是否重复
            Long count = this.lambdaQuery()
                    .eq(Planv2::getName, planv2DTO.getName())
                    .count();

            if (count > 0) {
                return CustomResult.fail("计划名称已存在，请重新输入");
            }

            // 3. DTO 转换为实体
            Planv2 planv2 = new Planv2();
            BeanUtils.copyProperties(planv2DTO, planv2);
            planv2.setCreateTime(new Date());
            planv2.setUpdateTime(new Date());

            // 4. 插入计划
            boolean saved = this.save(planv2);
            if (!saved) {
                return CustomResult.fail("保存计划失败");
            }

            // 5. 关联故障配置
            if (faultConfigIdList != null && faultConfigIdList.length > 0) {
                for (int faultConfigId : faultConfigIdList) {
                    Faultcorrelation faultcorrelation = new Faultcorrelation();
                    faultcorrelation.setPlanId(planv2.getId());
                    faultcorrelation.setFaultConfigId(faultConfigId);
                    faultcorrelationService.save(faultcorrelation);
                }
            }

            return CustomResult.ok(planv2.getId());
        } catch (Exception e) {
            log.error("新增计划失败", e);
            return CustomResult.fail("新增计划失败: " + e.getMessage());
        }
    }

    @Override
    public CustomResult queryPlanList(QueryCriteria criteria) {
        try {
            // 1. 参数校验
            if (criteria.getPageNum() == null || criteria.getPageNum() < 1) {
                criteria.setPageNum(1);
            }
            if (criteria.getPageSize() == null || criteria.getPageSize() < 1) {
                criteria.setPageSize(10);
            }

            // 2. 构建查询条件
            QueryWrapper<Planv2> queryWrapper = new QueryWrapper<>();

            // 名称模糊查询
            if (StringUtils.isNotBlank(criteria.getName())) {
                queryWrapper.like("name", criteria.getName());
            }

            // 创建者模糊查询 - 需要先通过用户名查找对应的用户ID列表
            if (StringUtils.isNotBlank(criteria.getCreator())) {
                // 根据用户名查找用户ID
                List<Integer> creatorIds = usersService.getIdsByName(criteria.getCreator());
                if (!creatorIds.isEmpty()) {
                    queryWrapper.in("creator_id", creatorIds);
                } else {
                    // 如果根据名字找不到任何用户，直接返回空结果
                    Page<Planv2ListItemDTO> emptyPage = new Page<>(criteria.getPageNum(), criteria.getPageSize());
                    emptyPage.setRecords(new ArrayList<>());
                    emptyPage.setTotal(0);
                    return CustomResult.ok(emptyPage);
                }
            }

            // 计划类型精确查询
            if (StringUtils.isNotBlank(criteria.getPlanType())) {
                queryWrapper.eq("plan_type", criteria.getPlanType());
            }

            // 创建时间区间查询
            if (criteria.getStartTime() != null) {
                queryWrapper.ge("create_time", criteria.getStartTime());
            }
            if (criteria.getEndTime() != null) {
                queryWrapper.le("create_time", criteria.getEndTime());
            }

            // 排序处理
            if ("ASC".equalsIgnoreCase(criteria.getOrderByTime())) {
                queryWrapper.orderByAsc("create_time");
            } else {
                queryWrapper.orderByDesc("create_time");
            }

            // 3. 执行分页查询
            Page<Planv2> page = new Page<>(criteria.getPageNum(), criteria.getPageSize());
            Page<Planv2> planPage = planv2Mapper.selectPage(page, queryWrapper);

            // 4. 数据转换
            List<Planv2ListItemDTO> dtoList = new ArrayList<>();
            for (Planv2 plan : planPage.getRecords()) {
                try {
                    Planv2ListItemDTO dto = new Planv2ListItemDTO();
                    dto.setId(plan.getId());
                    dto.setName(plan.getName());
                    dto.setPlanType(plan.getPlanType());
                    dto.setCreateTime(plan.getCreateTime());

                    // 获取创建者名称
                    String creatorName = usersService.getRealNameById(plan.getCreatorId());
                    dto.setCreator(creatorName != null ? creatorName : "未知用户");

                    // 获取故障数量
                    try {
                        Workflow workflow = workflowService.getWorkflowById(plan.getWorkflowId());
                        if (workflow != null && StringUtils.isNotBlank(workflow.getContent())) {
                            // 将JSON字符串解析为Jackson对象
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode rootNode = objectMapper.readTree(workflow.getContent());

                            // 获取templates数组
                            JsonNode templatesNode = rootNode.path("spec").path("templates");

                            // 计数匹配"templateType": "FaultConfig"的模板数量
                            int faultCount = 0;
                            if (templatesNode.isArray()) {
                                for (JsonNode template : templatesNode) {
                                    String templateType = template.path("templateType").asText();
                                    if ("FaultConfig".equals(templateType)) {
                                        faultCount++;
                                    }
                                }
                            }

                            dto.setFaultNum(faultCount);
                        } else {
                            dto.setFaultNum(0);
                        }
                    } catch (Exception e) {
                        log.error("获取故障数量失败，planId={}", plan.getId(), e);
                        dto.setFaultNum(0);
                    }

                    // 获取稳态数量
                    try {
                        QueryWrapper<Statebound> stateboundQuery = new QueryWrapper<>();
                        stateboundQuery.eq("bound_type", 0)
                                .eq("bound_id", plan.getId().toString());
                        long steadyCount = stateboundService.count(stateboundQuery);
                        dto.setSteadyNum((int) steadyCount);
                    } catch (Exception e) {
                        log.error("获取稳态数量失败，planId={}", plan.getId(), e);
                        dto.setSteadyNum(0);
                    }

                    dtoList.add(dto);
                } catch (Exception e) {
                    log.error("转换计划数据失败，planId={}", plan.getId(), e);
                    // 继续处理下一条记录
                }
            }

            // 5. 构建返回对象
            Page<Planv2ListItemDTO> resultPage = new Page<>(criteria.getPageNum(), criteria.getPageSize());
            resultPage.setRecords(dtoList);
            resultPage.setTotal(planPage.getTotal());

            return CustomResult.ok(resultPage);

        } catch (Exception e) {
            log.error("查询计划列表失败", e);
            return CustomResult.fail("查询计划列表失败: " + e.getMessage());
        }
    }

    @Override
    public CustomResult getPlanDetail(Integer planId) {
        try {
            // 1. 获取计划基本信息
            Planv2 plan = planv2Mapper.selectById(planId);
            if (plan == null) {
                return CustomResult.fail("计划不存在");
            }

            Planv2DetailInfo detailInfo = new Planv2DetailInfo();

            // 2. 设置基本信息
            BasicInfo basicInfo = new BasicInfo();
            basicInfo.setName(plan.getName());
            basicInfo.setPlanType(plan.getPlanType());
            basicInfo.setExcuteTime(plan.getExcuteTime() != null
                    ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(plan.getExcuteTime())
                    : "");
            basicInfo.setSceneDesc(plan.getSceneDesc());
            basicInfo.setExpection(plan.getExpection());
            basicInfo.setLoadId(plan.getLoadId());
            basicInfo.setGraph(plan.getGraph());
            basicInfo.setDuration(plan.getDuration());
            // 获取 Load 信息
            if (StringUtils.isNotBlank(plan.getLoadId())) {
                Load load = loadService.getById(plan.getLoadId());
                if (load != null) {
                    basicInfo.setApplicationId(load.getApplicationId());
                }
            }
            // 获取workflow脚本
            basicInfo.setWorkflowScript(workflowService.getWorkflowById(plan.getWorkflowId()).getContent());
            // 获取稳态列表
            QueryWrapper<Statebound> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("bound_id", planId.toString())
                    .eq("bound_type", 0);
            List<Statebound> statebounds = stateboundService.list(queryWrapper);
            List<String> steadyIdList = statebounds.stream()
                    .map(Statebound::getSteadyId)
                    .collect(Collectors.toList());
            basicInfo.setSteadyIdList(steadyIdList);

            detailInfo.setBasicInfo(basicInfo);

            // 3. 设置记录信息
            QueryWrapper<ChaosExerciseRecords> chaosExerciseRecordsQueryWrapper = new QueryWrapper<>();
            chaosExerciseRecordsQueryWrapper.eq("plan_id", planId);
            Page<ChaosExerciseRecords> recordPage = new Page<>(1, 20);
            Page<ChaosExerciseRecords> records = chaosExerciseRecordsService.page(recordPage,
                    chaosExerciseRecordsQueryWrapper);

            PlanRecordInfo recordInfo = new PlanRecordInfo();
            List<RecordInfo> recordInfoList = records.getRecords().stream()
                    .map(record -> {
                        RecordInfo info = new RecordInfo();
                        info.setId(record.getId());
                        info.setPressStatus(1); // 默认设为1
                        info.setRecordStatus(record.getRecordStatus().toString());
                        // 获取执行人信息
                        info.setPlayer(record.getPlayerName());
                        info.setStartTime(record.getStartTime());
                        info.setEndTime(record.getEndTime());
                        return info;
                    }).collect(Collectors.toList());

            recordInfo.setRecords(recordInfoList);
            recordInfo.setTotal(records.getTotal());
            recordInfo.setSize(records.getSize());
            recordInfo.setCurrent(records.getCurrent());
            recordInfo.setPages(records.getPages());

            detailInfo.setPlanRecordInfo(recordInfo);

            // 4. 设置其他信息
            OtherInfo otherInfo = new OtherInfo();
            Users creator = usersService.getById(plan.getCreatorId());
            otherInfo.setCreator(creator != null ? creator.getRealName() : "未知");
            otherInfo.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(plan.getCreateTime()));
            otherInfo.setUpdateTime(plan.getUpdateTime() != null
                    ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(plan.getUpdateTime())
                    : "");

            detailInfo.setOtherInfo(otherInfo);

            return CustomResult.ok(detailInfo);
        } catch (Exception e) {
            log.error("获取计划详情失败，planId={}", planId, e);
            return CustomResult.fail("获取计划详情失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomResult updatePlan(UpdatePlanDTO updateDTO) {
        try {
            Integer planId = updateDTO.getPlanv2DTO().getId();

            // 1. 检查计划是否存在
            Planv2 existingPlan = this.getById(planId);
            if (existingPlan == null) {
                return CustomResult.fail("计划不存在");
            }

            // 2. 检查是否有演练记录
            long recordCount = chaosExerciseRecordsService.lambdaQuery()
                    .eq(ChaosExerciseRecords::getPlanId, planId)
                    .count();

            if (recordCount > 0) {
                return CustomResult.fail("已有演练记录的计划不能进行更新");
            }

            // 3. 更新计划基本信息
            Planv2 planv2 = new Planv2();
            BeanUtils.copyProperties(updateDTO, planv2);
            planv2.setUpdateTime(new Date());

            boolean updated = this.updateById(planv2);
            if (!updated) {
                return CustomResult.fail("更新计划基本信息失败");
            }

            // 4. 更新工作流
            if (updateDTO.getWorkflowDTO() != null) {
                Workflow workflow = workflowService.getWorkflowById(planv2.getWorkflowId());
                if (workflow != null) {
                    workflow.setContent(updateDTO.getWorkflowDTO().getContent());
                    workflow.setNodes(updateDTO.getWorkflowDTO().getNodes());
                    workflowService.updateById(workflow);
                }
            }

            // 5. 更新故障配置关联
            if (updateDTO.getFaultConfigIdList() != null) {
                // 先删除旧的关联
                QueryWrapper<Faultcorrelation> wrapper = new QueryWrapper<>();
                wrapper.eq("plan_id", planId);
                faultcorrelationService.remove(wrapper);

                // 添加新的关联
                for (Integer faultConfigId : updateDTO.getFaultConfigIdList()) {
                    Faultcorrelation correlation = new Faultcorrelation();
                    correlation.setPlanId(planId);
                    correlation.setFaultConfigId(faultConfigId);
                    faultcorrelationService.save(correlation);
                }
            }

            // 6. 更新稳态关联
            if (updateDTO.getSteadyIdList() != null) {
                // 先删除旧的关联
                QueryWrapper<Statebound> stateboundWrapper = new QueryWrapper<>();
                stateboundWrapper.eq("bound_id", planId)
                        .eq("bound_type", 0);
                stateboundService.remove(stateboundWrapper);

                // 添加新的关联
                for (String steadyId : updateDTO.getSteadyIdList()) {
                    Statebound statebound = new Statebound();
                    statebound.setBoundType(0);
                    statebound.setBoundId(String.valueOf(planId));
                    statebound.setSteadyId(steadyId);
                    stateboundService.save(statebound);
                }
            }

            return CustomResult.ok();
        } catch (Exception e) {
            log.error("更新计划失败，planId={}", updateDTO.getPlanv2DTO().getId(), e);
            return CustomResult.fail("更新计划失败: " + e.getMessage());
        }
    }

    /**
     * 获取计划更新信息
     */
    @Override
    public CustomResult getPlanUpdateInfo(String planId) {
        try {
            AddPlanv2DTO updateInfo = new AddPlanv2DTO();

            // 1. 获取计划基本信息
            Planv2 planv2 = this.getById(planId);
            if (planv2 == null) {
                return CustomResult.fail("计划不存在");
            }

            // 转换为 DTO
            Planv2DTO planv2DTO = new Planv2DTO();
            BeanUtils.copyProperties(planv2, planv2DTO);
            updateInfo.setPlanv2DTO(planv2DTO);

            // 2. 获取工作流信息
            if (planv2.getWorkflowId() != null) {
                Workflow workflow = workflowService.getWorkflowById(planv2.getWorkflowId());
                if (workflow != null) {
                    WorkflowDTO workflowDTO = new WorkflowDTO();
                    workflowDTO.setId(workflow.getId());
                    workflowDTO.setContent(workflow.getContent());
                    workflowDTO.setNodes(workflow.getNodes());
                    updateInfo.setWorkflowDTO(workflowDTO);
                }
            }

            // 3. 获取故障配置ID列表
            QueryWrapper<Faultcorrelation> faultWrapper = new QueryWrapper<>();
            faultWrapper.eq("plan_id", planId);
            List<Faultcorrelation> faultCorrelations = faultcorrelationService.list(faultWrapper);

            if (!faultCorrelations.isEmpty()) {
                int[] faultConfigIds = faultCorrelations.stream()
                        .mapToInt(Faultcorrelation::getFaultConfigId)
                        .toArray();
                updateInfo.setFaultConfigIdList(faultConfigIds);
            }

            // 4. 获取稳态ID列表
            QueryWrapper<Statebound> stateboundWrapper = new QueryWrapper<>();
            stateboundWrapper.eq("bound_id", planId)
                    .eq("bound_type", 0);
            List<Statebound> statebounds = stateboundService.list(stateboundWrapper);

            if (!statebounds.isEmpty()) {
                List<String> steadyIdList = statebounds.stream()
                        .map(Statebound::getSteadyId)
                        .collect(Collectors.toList());
                updateInfo.setSteadyIdList(steadyIdList);
            }

            return CustomResult.ok(updateInfo);

        } catch (Exception e) {
            log.error("获取计划更新信息失败，planId={}", planId, e);
            return CustomResult.fail("获取计划更新信息失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomResult deletePlan(Integer planId) {
        try {
            // 1. 验证计划是否存在
            Planv2 plan = this.getById(planId);
            if (plan == null) {
                return CustomResult.fail("计划不存在");
            }

            // 2. 删除记录信息
            QueryWrapper<ChaosExerciseRecords> recordWrapper = new QueryWrapper<>();
            recordWrapper.eq("plan_id", planId);
            chaosExerciseRecordsService.remove(recordWrapper);

            // 3. 删除故障配置关联
            QueryWrapper<Faultcorrelation> faultWrapper = new QueryWrapper<>();
            faultWrapper.eq("plan_id", planId);
            faultcorrelationService.remove(faultWrapper);

            // 4. 删除稳态关联
            QueryWrapper<Statebound> stateboundWrapper = new QueryWrapper<>();
            stateboundWrapper.eq("bound_id", planId.toString())
                    .eq("bound_type", 0);
            stateboundService.remove(stateboundWrapper);

            // 5. 删除演练记录
            QueryWrapper<ChaosExerciseRecords> exerciseWrapper = new QueryWrapper<>();
            exerciseWrapper.eq("plan_id", planId);
            chaosExerciseRecordsService.remove(exerciseWrapper);

            // 6. 删除工作流
            if (plan.getWorkflowId() != null) {
                workflowService.removeById(plan.getWorkflowId());
            }

            // 7. 删除计划本身
            boolean removed = this.removeById(planId);
            if (!removed) {
                throw new RuntimeException("删除计划失败");
            }

            return CustomResult.ok("删除计划成功");

        } catch (Exception e) {
            log.error("删除计划失败，planId={}", planId, e);
            throw new RuntimeException("删除计划失败: " + e.getMessage());
        }
    }

    @Override
    public CustomResult executePlan(Planv2 planv2, String playerName) {
        log.info("开始执行计划，计划ID：{}", planv2.getId());
        ChaosExerciseRecords record = initializeRecord(planv2, playerName);
        String recordId = record.getId().toString();

        try {
            executeFullPlan(planv2, record);
            return CustomResult.ok(record.getId());
        } catch (Exception e) {
            log.error("执行计划失败，planId={}", planv2.getId(), e);
            if (record != null && recordId != null) {
                logExercise(recordId, "执行失败：" + e.getMessage(), "ERROR", "PLAN");
                record.setRecordStatus(3);
                chaosExerciseRecordsService.updateById(record);
            }
            return CustomResult.fail("执行失败: " + e.getMessage());
        }
    }

    // 如果您仍然需要通过ID执行计划的方法，可以保留这个方法，但不要使用@Override注解
    public CustomResult executePlanById(Integer planId) {
        log.info("通过ID开始执行计划，计划ID：{}", planId);

        try {
            // 1. 获取计划信息
            Planv2 plan = this.getById(planId);
            if (plan == null) {
                return CustomResult.fail("计划不存在");
            }
            // 2. 调用主要的执行方法
            // TODO： 系统不对
            return executePlan(plan, "系统");
        } catch (Exception e) {
            log.error("通过ID执行计划失败，planId={}", planId, e);
            return CustomResult.fail("执行失败: " + e.getMessage());
        }
    }

    private ChaosExerciseRecords initializeRecord(Planv2 planv2, String playerName) {
        log.info("开始初始化演练记录，计划ID：{}", planv2.getId());
        ChaosExerciseRecords record = new ChaosExerciseRecords();
        record.setCreatedTime(new Date());
        record.setPlanId(planv2.getId());
        record.setRecordStatus(0); // 未运行
        record.setPlayerName(playerName);
        chaosExerciseRecordsService.save(record);
        log.info("演练记录初始化完成，记录ID：{}", record.getId());
        return record;
    }

    private void executeFullPlan(Planv2 planv2, ChaosExerciseRecords record) throws Exception {
        // 解析duration，转换为毫秒
        long durationMs = parseDuration(planv2.getDuration());
        String loadId = planv2.getLoadId();
        String recordId = record.getId().toString();

        logExercise(recordId, "开始执行计划：" + planv2.getName(), "INFO", "PLAN");
        logExercise(recordId, "计划执行时长：" + durationMs + "毫秒", "INFO", "PLAN");

        try {
            if (loadId == null || loadId.isEmpty()) {
                // 无压测模式的状态: 0(未运行) -> 1(故障注入中) -> 2(执行成功)/3(执行失败)
                logExercise(recordId, "无压测任务，直接执行故障注入", "INFO", "PLAN");
                String workflowName = generateWorkflowName();
                logExercise(recordId, "生成工作流名称：" + workflowName, "INFO", "WORKFLOW");
                startTimestamp = new Timestamp(System.currentTimeMillis());
                executeInjection(planv2, record, workflowName);

                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                scheduler.schedule(() -> {
                    try {
                        // 无压测模式：成功为2，失败为3
                        updateRecordStatus(record, workflowName, false);
                    } finally {
                        scheduler.shutdown();
                        logExercise(recordId, "执行完成，定时任务已关闭", "INFO", "PLAN");
                    }
                }, durationMs, TimeUnit.MILLISECONDS);

            } else {
                // 有压测模式的状态: 0(未运行) -> 1(初始压测) -> 2(故障注入) -> 3(收尾压测) -> 4(成功)/5(失败)
                startTimestamp = new Timestamp(System.currentTimeMillis());
                logExercise(recordId, "开始执行完整流程（压测+故障注入），压测ID：" + loadId, "INFO", "PLAN");
                executeFullProcess(planv2, record, durationMs);
            }

        } catch (Exception e) {
            logExercise(recordId, "执行失败：" + e.getMessage(), "ERROR", "PLAN");
            // 根据是否有压测设置不同的失败状态
            if (loadId == null || loadId.isEmpty()) {
                record.setRecordStatus(3); // 无压测模式失败
            } else {
                record.setRecordStatus(5); // 有压测模式失败
            }
            chaosExerciseRecordsService.updateById(record);
            throw new RuntimeException("执行失败");
        }
    }

    private void executeInjection(Planv2 planv2, ChaosExerciseRecords record, String workflowName) {
        String recordId = record.getId().toString();
        Random random = new Random();
        long randomDelay = random.nextInt(71) + 50; // 71 = 120 - 50 + 1
        Timestamp injectionTimestamp = new Timestamp(startTimestamp.getTime() + randomDelay);

        logExercise(recordId, "开始执行故障注入", "INFO", "INJECTION", injectionTimestamp);

        Integer workflowId = planv2.getWorkflowId();
        logExercise(recordId, "获取工作流信息，工作流ID：" + workflowId, "INFO", "WORKFLOW");

        Workflow workflow = workflowService.getById(workflowId);
        if (workflow == null) {
            logExercise(recordId, "工作流不存在", "ERROR", "WORKFLOW");
            throw new RuntimeException("工作流不存在");
        }

        // 创建关联
        logExercise(recordId, "创建工作流关联信息", "INFO", "WORKFLOW");
        Workflowcorrelation correlation = new Workflowcorrelation();
        correlation.setName(workflowName);
        correlation.setWorkflowId(workflowId);
        correlation.setRecordId(record.getId());
        correlationService.save(correlation);
        logExercise(recordId, "工作流关联创建完成", "INFO", "WORKFLOW");

        // 更新记录状态
        String loadId = planv2.getLoadId();
        if (loadId == null || loadId.isEmpty()) {
            // 无压测模式，直接设置为故障注入中(1)
            record.setRecordStatus(1);
        } else {
            // 有压测模式，设置为故障注入中(2)
            record.setRecordStatus(2);
        }
        record.setStartTime(new Date());
        chaosExerciseRecordsService.updateById(record);
        logExercise(recordId, "记录状态已更新为进行中，状态码：" + record.getRecordStatus(), "INFO", "RECORD");

        // 执行工作流
        logExercise(recordId, "开始执行工作流：" + workflowName, "INFO", "WORKFLOW");
        workflowUtils.executeWorkflow(workflow.getContent(), workflowName);
        logExercise(recordId, "工作流执行请求已发送", "INFO", "WORKFLOW");
    }

    private void executeFullProcess(Planv2 planv2, ChaosExerciseRecords record, long durationMs) throws Exception {
        String recordId = record.getId().toString();
        logExercise(recordId, "开始执行完整流程", "INFO", "PLAN");

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        String workflowName = generateWorkflowName();
        logExercise(recordId, "生成工作流名称：" + workflowName, "INFO", "WORKFLOW");

        // 第一阶段压测 - 设置状态为初始压测中(1)
        logExercise(recordId, "开始第一阶段压测", "INFO", "LOAD_TEST");
        record.setRecordStatus(1); // 初始压测中
        record.setStartTime(new Date());
        chaosExerciseRecordsService.updateById(record);
        logExercise(recordId, "记录状态已更新为初始压测中", "INFO", "RECORD");

        String loadTaskId1 = loadTestUtils.startLoadTest(planv2.getLoadId());
        // 记录第一阶段压测关联
        saveLoadCorrelation(loadTaskId1, record.getId());
        log.info("保存压测关联关系成功，loadTaskId: " + loadTaskId1);

        executor.schedule(() -> {
            logExercise(recordId, "第一阶段压测结束", "INFO", "LOAD_TEST");
            loadTestUtils.stopLoadTest(loadTaskId1);

            // 第一阶段结束后暂停10秒
            pauseBetweenLoadTests(recordId, (long) (durationMs * 0.2));

            // 第二阶段：压测+故障注入 - 设置状态为故障注入中(2)
            startTimestamp = new Timestamp(System.currentTimeMillis());
            logExercise(recordId, "开始第二阶段：压测+故障注入", "INFO", "PLAN");
            try {
                String loadTaskId2;
                loadTaskId2 = loadTestUtils.startLoadTest(planv2.getLoadId());
                // 记录第二阶段压测关联
                executeInjection(planv2, record, workflowName);
                saveLoadCorrelation(loadTaskId2, record.getId());
                executor.schedule(() -> {
                    logExercise(recordId, "第二阶段结束，检查执行状态", "INFO", "PLAN");
                    loadTestUtils.stopLoadTest(loadTaskId2);

                    // 第二阶段结束后暂停10秒
                    pauseBetweenLoadTests(recordId, (long) (durationMs * 0.2));

                    // 第三阶段压测 - 设置状态为收尾压测中(3)
                    record.setRecordStatus(3); // 收尾压测中
                    chaosExerciseRecordsService.updateById(record);
                    logExercise(recordId, "记录状态已更新为收尾压测中", "INFO", "RECORD");

                    logExercise(recordId, "开始第三阶段压测", "INFO", "LOAD_TEST");
                    try {
                        String loadTaskId3;
                        loadTaskId3 = loadTestUtils.startLoadTest(planv2.getLoadId());
                        // 记录第三阶段压测关联
                        saveLoadCorrelation(loadTaskId3, record.getId());

                        executor.schedule(() -> {
                            logExercise(recordId, "第三阶段压测结束", "INFO", "LOAD_TEST");
                            loadTestUtils.stopLoadTest(loadTaskId3);

                            // 更新最终状态 - 有压测模式下，成功为4
                            updateRecordStatus(record, workflowName, true);

                            logExercise(recordId, "完整流程执行完毕", "INFO", "PLAN");
                            executor.shutdown();
                        }, durationMs, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        logExercise(recordId, "压测启动失败: " + e.getMessage(), "ERROR", "LOAD_TEST");
                        record.setRecordStatus(5); // 有压测模式失败
                        record.setEndTime(new Date());
                        chaosExerciseRecordsService.updateById(record);
                        e.printStackTrace();
                    }

                }, durationMs, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logExercise(recordId, "压测启动失败: " + e.getMessage(), "ERROR", "LOAD_TEST");
                record.setRecordStatus(5); // 有压测模式失败
                record.setEndTime(new Date());
                chaosExerciseRecordsService.updateById(record);
                e.printStackTrace();
            }

        }, durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 保存压测任务与演练记录的关联关系
     */
    private void saveLoadCorrelation(String loadTaskId, Integer recordId) {
        if (loadTaskId != null && !loadTaskId.isEmpty()) {
            try {
                LoadCorrelation correlation = new LoadCorrelation();
                correlation.setLoadTaskId(loadTaskId);
                correlation.setChaosExerciseRecordId(recordId);
                loadCorrelationService.save(correlation);
                logExercise(recordId.toString(),
                        "保存压测关联关系成功，loadTaskId: " + loadTaskId,
                        "INFO",
                        "LOAD_TEST");
            } catch (Exception e) {
                logExercise(recordId.toString(),
                        "保存压测关联关系失败: " + e.getMessage(),
                        "ERROR",
                        "LOAD_TEST");
            }
        }
    }

    private void updateRecordStatus(ChaosExerciseRecords record, String workflowName, boolean hasLoadTest) {
        String recordId = record.getId().toString();
        logExercise(recordId, "等待10秒后开始检查工作流状态", "INFO", "WORKFLOW");
        try {
            Thread.sleep(10000);
            WorkflowSummary summary = workflowUtils.getWorkflowSummary(workflowName);

            if (workflowUtils.isWorkflowSuccessful(summary)) {
                logExercise(recordId, "工作流执行成功", "INFO", "WORKFLOW");
                if (hasLoadTest) {
                    record.setRecordStatus(4); // 有压测模式成功
                } else {
                    record.setRecordStatus(2); // 无压测模式成功
                }
            } else {
                logExercise(recordId, "工作流执行失败", "ERROR", "WORKFLOW");
                if (hasLoadTest) {
                    record.setRecordStatus(5); // 有压测模式失败
                } else {
                    record.setRecordStatus(3); // 无压测模式失败
                }
            }

            record.setEndTime(new Date());
            chaosExerciseRecordsService.updateById(record);
            logExercise(recordId, "记录状态已更新，最终状态：" + record.getRecordStatus(), "INFO", "RECORD");

        } catch (InterruptedException e) {
            logExercise(recordId, "状态更新等待被中断: " + e.getMessage(), "ERROR", "WORKFLOW");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logExercise(recordId, "更新记录状态失败: " + e.getMessage(), "ERROR", "RECORD");
            if (hasLoadTest) {
                record.setRecordStatus(5); // 有压测模式失败
            } else {
                record.setRecordStatus(3); // 无压测模式失败
            }
            record.setEndTime(new Date());
            chaosExerciseRecordsService.updateById(record);
        }
    }

    private String generateWorkflowName() {
        String workflowName = "workflow-" + UUID.randomUUID().toString().substring(0, 8);
        log.debug("生成工作流名称：{}", workflowName);
        return workflowName;
    }

    private long parseDuration(String duration) {
        log.debug("解析持续时间：{}", duration);
        String value = duration.substring(0, duration.length() - 1);
        int seconds = Integer.parseInt(value);
        long milliseconds = seconds * 1000L;
        log.debug("持续时间解析结果：{}毫秒", milliseconds);
        return milliseconds;
    }

    /**
     * 获取计划故障注入对象数量
     * 
     * @param planId 计划ID
     * @return TargetObjectNumDTO
     */
    @Override
    public TargetObjectNumDTO getFaultInjectObjectNum(Integer planId) {
        try {
            // 1. 获取计划信息
            Planv2 plan = this.getById(planId);
            if (plan == null) {
                log.error("计划不存在，planId={}", planId);
                return new TargetObjectNumDTO(0);
            }

            // 2. 获取工作流信息
            Workflow workflow = workflowService.getById(plan.getWorkflowId());
            if (workflow == null || StringUtils.isBlank(workflow.getContent())) {
                log.error("工作流不存在或内容为空，workflowId={}", plan.getWorkflowId());
                return new TargetObjectNumDTO(0);
            }

            // 3. 解析工作流内容
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(workflow.getContent());
            JsonNode templatesNode = rootNode.path("spec").path("templates");

            // 4. 提取所有选择器并去重
            Set<String> uniqueSelectors = new HashSet<>();
            int totalTargets = 0;

            if (templatesNode.isArray()) {
                for (JsonNode template : templatesNode) {
                    if ("FaultConfig".equals(template.path("templateType").asText())) {
                        JsonNode listNode = template.path("list");
                        if (listNode.isArray()) {
                            for (JsonNode faultNode : listNode) {
                                if ("Inject".equals(faultNode.path("type").asText())) {
                                    // 获取故障类型
                                    String chaosType = faultNode.path("chaosType").asText();

                                    // 根据故障类型获取选择器
                                    JsonNode selectorNode = null;
                                    if (faultNode.has(chaosType)) {
                                        selectorNode = faultNode.path(chaosType).path("selector");
                                    }

                                    if (selectorNode != null && !selectorNode.isMissingNode()) {
                                        // 提取命名空间和标签选择器
                                        String namespace = selectorNode.path("namespaces").isArray()
                                                ? selectorNode.path("namespaces").get(0).asText()
                                                : "default";

                                        Map<String, String> labelSelectors = new HashMap<>();
                                        JsonNode labelSelectorsNode = selectorNode.path("labelSelectors");
                                        if (labelSelectorsNode.isObject()) {
                                            Iterator<Map.Entry<String, JsonNode>> fields = labelSelectorsNode.fields();
                                            while (fields.hasNext()) {
                                                Map.Entry<String, JsonNode> entry = fields.next();
                                                labelSelectors.put(entry.getKey(), entry.getValue().asText());
                                            }
                                        }

                                        // 创建唯一标识
                                        String selectorKey = namespace + ":" + labelSelectors.toString();

                                        // 如果是新的选择器，查询对应的Pod数量
                                        if (!uniqueSelectors.contains(selectorKey)) {
                                            uniqueSelectors.add(selectorKey);

                                            // 调用K8s API获取Pod数量
                                            int podCount = getPodCount(namespace, labelSelectors);
                                            totalTargets += podCount;

                                            log.debug("命名空间: {}, 标签: {}, Pod数量: {}",
                                                    namespace, labelSelectors, podCount);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            log.info("计划ID: {}, 故障注入对象总数: {}", planId, totalTargets);
            return new TargetObjectNumDTO(totalTargets);

        } catch (Exception e) {
            log.error("获取故障注入对象数量失败，planId={}", planId, e);
            return new TargetObjectNumDTO(0);
        }
    }

    /**
     * 根据命名空间和标签选择器获取Pod数量
     * 
     * @param namespace      命名空间
     * @param labelSelectors 标签选择器
     * @return Pod数量
     */
    private int getPodCount(String namespace, Map<String, String> labelSelectors) {
        try {
            // 使用 KubernetesClient 获取 Pod 列表
            return kubernetesClient.pods()
                    .inNamespace(namespace)
                    .withLabels(labelSelectors) // 直接使用Map对象
                    .list()
                    .getItems()
                    .size();

        } catch (Exception e) {
            log.error("获取Pod数量失败，namespace={}, labelSelectors={}", namespace, labelSelectors, e);
            return 0;
        }
    }
}
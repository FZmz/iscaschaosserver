package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Application;
import com.iscas.lndicatormonitor.domain.Faultcorrelation;
import com.iscas.lndicatormonitor.domain.Planv2;
import com.iscas.lndicatormonitor.domain.Workflow;
import com.iscas.lndicatormonitor.domain.Workflowcorrelation;
import com.iscas.lndicatormonitor.domain.recordv2.ChaosExerciseRecords;
import com.iscas.lndicatormonitor.dto.planv2.RecordInfoDTO;
import com.iscas.lndicatormonitor.dto.planv2.TargetObjectNumDTO;
import com.iscas.lndicatormonitor.dto.recordv2.ChaosExeRecordsDTO;
import com.iscas.lndicatormonitor.dto.recordv2.RecordListDTO;
import com.iscas.lndicatormonitor.dto.recordv2.Recordv2QueryCriteria;
import com.iscas.lndicatormonitor.dto.recordv2.WorkflowSummaryDTO;
import com.iscas.lndicatormonitor.mapper.ChaosExerciseRecordsMapper;
import com.iscas.lndicatormonitor.service.*;
import com.iscas.lndicatormonitor.utils.WorkflowUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * @author mj
 * @description 针对表【chaos_exercise_records(混沌工程演练记录表)】的数据库操作Service实现
 * @createDate 2025-01-19 22:41:43
 */
@Service
@Slf4j
public class ChaosExerciseRecordsServiceImpl extends ServiceImpl<ChaosExerciseRecordsMapper, ChaosExerciseRecords>
        implements ChaosExerciseRecordsService {

    @Autowired
    private Planv2Service planv2Service;

    @Autowired
    private ApplicationService applicationService;

    @Value("${platform.workflowaddr}")
    private String workflowAddr;

    @Value("${chaos.mesh.url}")
    private String chaosMeshUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${chaos.mesh.token}")
    private String chaosMeshToken;

    @Autowired
    private FaultcorrelationService faultcorrelationService;
    @Autowired
    private WorkflowcorrelationService workflowcorrelationService;

    @Autowired
    private WorkflowUtils workflowUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkflowService workflowService;

    @Override
    public boolean save(ChaosExerciseRecords record) {
        // 调用父类的save方法
        return super.save(record);
    }

    @Override
    public ChaosExeRecordsDTO getBasicInfo(Integer recordId) {
        log.info("开始获取演练记录基本信息, recordId={}", recordId);
        try {
            // 1. 获取演练记录信息
            log.debug("正在查询演练记录信息...");
            ChaosExerciseRecords record = this.getById(recordId);
            if (record == null) {
                log.error("演练记录不存在, recordId={}", recordId);
                throw new RuntimeException("演练记录不存在");
            }
            log.debug("成功获取演练记录: {}", record);

            // 2. 创建返回DTO
            log.debug("开始构建返回DTO...");
            ChaosExeRecordsDTO dto = new ChaosExeRecordsDTO();
            dto.setId(record.getId());
            dto.setCreateTime(record.getCreatedTime());
            dto.setPlayerName(record.getPlayerName());
            dto.setRecordStatus(record.getRecordStatus());
            // 获取故障注入对象数量
            log.debug("正在获取故障注入对象数量...");
            TargetObjectNumDTO targetObjectNum = planv2Service.getFaultInjectObjectNum(record.getPlanId());
            dto.setFaulInjectObjectNum(targetObjectNum.getObjectNum());
            log.info("故障注入对象数量: {}, planId={}", dto.getFaulInjectObjectNum(), record.getPlanId());

            // 根据recordId获取workflowName
            log.debug("正在查询工作流关联信息...");
            Workflowcorrelation workflowcorrelation = workflowcorrelationService.lambdaQuery()
                    .eq(Workflowcorrelation::getRecordId, recordId)
                    .orderByDesc(Workflowcorrelation::getId)
                    .last("LIMIT 1")
                    .one();
            if (workflowcorrelation == null) {
                log.error("未找到对应的工作流信息, recordId={}", recordId);
                throw new RuntimeException("未找到对应的工作流信息");
            }
            log.info("获取到工作流关联信息: {}", workflowcorrelation);

            // 获取工作流详细信息
            log.info("正在获取工作流详细信息, workflowName={}", workflowcorrelation.getName());
            RecordInfoDTO recordInfoDTO = workflowUtils.fillRecordInfo(workflowcorrelation.getName());
            if (recordInfoDTO == null) {
                log.error("未找到工作流详细信息, workflowName={}", workflowcorrelation.getName());
                throw new RuntimeException("未找到对应的工作流信息");
            }
            log.info("获取到工作流详细信息: {}", recordInfoDTO);

            try {
                log.debug("开始处理时间信息...");
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

                if (recordInfoDTO.getStartTime() != null) {
                    Date startTime = isoFormat.parse(recordInfoDTO.getStartTime());
                    dto.setStartTime(startTime);
                    record.setStartTime(startTime);
                    log.debug("设置开始时间: {}", startTime);
                }
                if (recordInfoDTO.getEndTime() != null) {
                    Date endTime = isoFormat.parse(recordInfoDTO.getEndTime());
                    dto.setEndTime(endTime);
                    record.setEndTime(endTime);
                    log.debug("设置结束时间: {}", endTime);
                }

                // 获取计划信息以检查是否有压测
                log.debug("正在获取计划信息, planId={}", record.getPlanId());
                Planv2 plan = planv2Service.getById(record.getPlanId());
                boolean hasLoad = plan != null && plan.getLoadId() != null && !plan.getLoadId().isEmpty();
                log.info("计划是否包含压测: {}", hasLoad);

                // 根据是否有压测设置不同的状态
                log.info("开始处理记录状态, 原始状态={}, 是否有压测={}", recordInfoDTO.getRecordStatus(), hasLoad);
                // if (hasLoad) {
                //     // 有压测时的状态映射
                //     switch (recordInfoDTO.getRecordStatus()) {
                //         case 0:
                //             dto.setRecordStatus(0);
                //             log.info("有压测-状态设置为未运行(0)");
                //             break;
                //         case 1:
                //             dto.setRecordStatus(1);
                //             log.info("有压测-状态设置为初始压测中(1)");
                //             break;
                //         case 2:
                //             dto.setRecordStatus(4);
                //             log.info("有压测-状态设置为成功(4)");
                //             break;
                //         case 3:
                //             dto.setRecordStatus(5);
                //             log.info("有压测-状态设置为失败(5)");
                //             break;
                //         default:
                //             dto.setRecordStatus(recordInfoDTO.getRecordStatus());
                //             log.info("有压测-使用原始状态({})", recordInfoDTO.getRecordStatus());
                //     }
                // } else {
                //     dto.setRecordStatus(recordInfoDTO.getRecordStatus());
                //     log.info("无压测-使用原始状态({})", recordInfoDTO.getRecordStatus());
                // }

                // if (record.getRecordStatus() != dto.getRecordStatus()) {
                //     log.info("更新记录状态: {} -> {}", record.getRecordStatus(), dto.getRecordStatus());
                //     record.setRecordStatus(dto.getRecordStatus());
                //     this.updateById(record);
                // }
            } catch (ParseException e) {
                log.error("时间格式转换失败: {}", e.getMessage(), e);
                throw new RuntimeException("时间格式转换失败", e);
            }

            // 3. 获取计划详细信息
            log.debug("开始获取计划详细信息...");
            Planv2 plan = planv2Service.getById(record.getPlanId());
            if (plan != null) {
                dto.setPlanName(plan.getName());
                dto.setPlanId(plan.getId());
                log.debug("获取到计划信息: planName={}, planId={}", plan.getName(), plan.getId());
                
                // 判断是否有压测
                String loadId = plan.getLoadId();
                dto.setIsHaveLoad(loadId != null && !loadId.isEmpty() ? 1 : 0);
                log.debug("计划压测状态: isHaveLoad={}, loadId={}", dto.getIsHaveLoad(), loadId);
                
                // 处理预计执行时长
                String duration = plan.getDuration();
                if (duration != null && duration.endsWith("s")) {
                    try {
                        int seconds = Integer.parseInt(duration.substring(0, duration.length() - 1));
                        dto.setExpectedExeTime(String.valueOf(seconds * 3));
                        log.debug("设置预计执行时长: {}s", seconds * 3);
                    } catch (NumberFormatException e) {
                        log.warn("解析执行时长失败, duration={}", duration);
                        dto.setExpectedExeTime("0");
                    }
                }
                
                // 获取故障数量
                log.debug("开始获取故障数量...");
                try {
                    Workflow workflow = workflowService.getWorkflowById(plan.getWorkflowId());
                    if (workflow != null && StringUtils.isNotBlank(workflow.getContent())) {
                        JsonNode rootNode = objectMapper.readTree(workflow.getContent());
                        JsonNode templatesNode = rootNode.path("spec").path("templates");
                        
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
                        log.debug("获取到故障数量: {}", faultCount);
                    } else {
                        log.warn("未找到工作流内容或工作流为空, workflowId={}", plan.getWorkflowId());
                        dto.setFaultNum(0);
                    }
                } catch (Exception e) {
                    log.error("获取故障数量失败, planId={}", plan.getId(), e);
                    dto.setFaultNum(0);
                }
                
                // 4. 获取故障配置信息
                log.debug("开始获取故障配置信息...");
                List<Faultcorrelation> faultCorrelations = faultcorrelationService.lambdaQuery()
                        .eq(Faultcorrelation::getPlanId, record.getPlanId())
                        .list();
                List<Integer> faultConfigIdList = faultCorrelations.stream()
                        .map(Faultcorrelation::getFaultConfigId)
                        .collect(Collectors.toList());
                dto.setFaultConfigIdList(faultConfigIdList);
                log.debug("获取到故障配置ID列表: {}", faultConfigIdList);

                // 5. 获取应用信息
                log.debug("开始获取应用信息...");
                Application app = applicationService.getById(plan.getApplicationId());
                if (app != null) {
                    dto.setTestedAppName(app.getName());
                    log.debug("获取到应用信息: {}", app.getName());
                } else {
                    log.warn("未找到应用信息, applicationId={}", plan.getApplicationId());
                }
            } else {
                log.warn("未找到计划信息, planId={}", record.getPlanId());
            }

            log.info("成功获取演练记录基本信息, recordId={}", recordId);
            return dto;
        } catch (Exception e) {
            log.error("获取演练记录基本信息失败, recordId={}", recordId, e);
            throw e;
        }
    }

    @Override
    public WorkflowSummaryDTO getWorkflowInfoByRecordId(Integer recordId) {
        // 1. 根据recordId获取工作流信息
        Workflowcorrelation correlation = workflowcorrelationService.lambdaQuery()
                .eq(Workflowcorrelation::getRecordId, recordId)
                .one();

        if (correlation == null) {
            throw new RuntimeException("未找到对应的工作流信息");
        }

        String workflowName = correlation.getName();
        String namespace = "sock-shop"; // 从配置或其他地方获取

        // 2. 构建请求URL
        String url = String.format("%s/api/real_time/workflow/%s/summary?namespace=%s",
                chaosMeshUrl, workflowName, namespace);

        try {
            // 3. 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", chaosMeshToken);
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            // 4. 发送HTTP请求
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    String.class);

            // 5. 解析响应
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());

            // 6. 构建返回对象
            WorkflowSummaryDTO summaryDTO = new WorkflowSummaryDTO();
            summaryDTO.setWorkflowName(rootNode.get("workflowName").asText());

            // 7. 处理nodeInfoList
            List<WorkflowSummaryDTO.NodeInfo> nodeInfoList = new ArrayList<>();
            JsonNode nodeInfoArray = rootNode.get("nodeInfoList");

            if (nodeInfoArray != null && nodeInfoArray.isArray()) {
                for (JsonNode nodeNode : nodeInfoArray) {
                    WorkflowSummaryDTO.NodeInfo nodeInfo = new WorkflowSummaryDTO.NodeInfo();
                    nodeInfo.setNodeName(nodeNode.get("nodeName").asText());
                    nodeInfo.setStartTime(nodeNode.get("startTime").asText());
                    nodeInfo.setEndTime(nodeNode.get("endTime").asText());
                    nodeInfo.setReason(nodeNode.get("reason").asText());

                    // 处理stepSpanList
                    List<WorkflowSummaryDTO.StepSpan> stepSpanList = new ArrayList<>();
                    JsonNode spanArray = nodeNode.get("stepSpanList");
                    if (spanArray != null && spanArray.isArray()) {
                        for (JsonNode spanNode : spanArray) {
                            WorkflowSummaryDTO.StepSpan span = new WorkflowSummaryDTO.StepSpan();
                            span.setStartTime(spanNode.get("startTime").asText());
                            span.setEndTime(spanNode.get("endTime").asText());
                            stepSpanList.add(span);
                        }
                    }
                    nodeInfo.setStepSpanList(stepSpanList);
                    nodeInfoList.add(nodeInfo);
                }
            }

            summaryDTO.setNodeInfoList(nodeInfoList);
            return summaryDTO;

        } catch (Exception e) {
            log.error("获取workflow信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取workflow信息失败: " + e.getMessage());
        }
    }

    @Override
    public IPage<RecordListDTO> queryRecordList(Recordv2QueryCriteria criteria) {
        log.info("查询条件: {}", criteria);  // 添加日志，查看传入的参数
        
        // 1. 如果需要按计划名称过滤，先获取符合条件的计划ID列表
        List<Integer> planIds = null;
        if (StringUtils.isNotBlank(criteria.getName())) {
            planIds = planv2Service.lambdaQuery()
                .like(Planv2::getName, criteria.getName())
                .list()
                .stream()
                .map(Planv2::getId)
                .collect(Collectors.toList());
            
            if (planIds.isEmpty()) {
                // 如果没有找到匹配的计划，直接返回空结果
                Page<RecordListDTO> emptyPage = new Page<>(criteria.getPageNum(), criteria.getPageSize());
                emptyPage.setTotal(0);
                return emptyPage;
            }
        }

        // 2. 构建查询条件
        QueryWrapper<ChaosExerciseRecords> wrapper = new QueryWrapper<>();
        
        // 添加计划ID条件
        if (planIds != null && !planIds.isEmpty()) {
            wrapper.in("plan_id", planIds);
        }
        
        // 处理创建时间条件 - 优先使用createdTime
        if (criteria.getCreatedTime() != null) {
            log.info("使用createdTime过滤: {}", criteria.getCreatedTime());  // 添加日志
            
            // 设置为当天开始时间 00:00:00
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(criteria.getCreatedTime());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startOfDay = calendar.getTime();
            
            // 设置为当天结束时间 23:59:59.999
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date endOfDay = calendar.getTime();
            
            log.info("过滤时间范围: {} 到 {}", startOfDay, endOfDay);  // 添加日志
            
            wrapper.ge("created_time", startOfDay);
            wrapper.le("created_time", endOfDay);
        } 
        // 如果没有createdTime，则使用startTime和endTime
        else {
            if (criteria.getStartTime() != null) {
                wrapper.ge("created_time", criteria.getStartTime());
            }
            if (criteria.getEndTime() != null) {
                wrapper.le("created_time", criteria.getEndTime());
            }
        }
        
        // 添加状态条件
        if (StringUtils.isNotBlank(criteria.getRecordStatus())) {
            wrapper.eq("record_status", criteria.getRecordStatus());
        }
        
        // 添加创建者条件
        if (StringUtils.isNotBlank(criteria.getCreator())) {
            wrapper.like("player_name", criteria.getCreator());
        }

        // 添加排序条件
        if ("DESC".equalsIgnoreCase(criteria.getOrderByTime())) {
            wrapper.orderByDesc("created_time");
        } else {
            wrapper.orderByAsc("created_time");
        }

        // 打印SQL语句
        log.info("执行SQL: {}", wrapper.getSqlSegment());
        
        // 3. 执行分页查询
        Page<ChaosExerciseRecords> page = new Page<>(criteria.getPageNum(), criteria.getPageSize());
        IPage<ChaosExerciseRecords> recordsPage = this.page(page, wrapper);
        
        log.info("查询结果总数: {}", recordsPage.getTotal());  // 添加日志
        
        // 4. 转换结果
        IPage<RecordListDTO> resultPage = recordsPage.convert(record -> {
            RecordListDTO dto = new RecordListDTO();
            dto.setId(record.getId());
            dto.setRecordStatus(record.getRecordStatus());
            dto.setCreatorName(record.getPlayerName());
            dto.setCreatedTime(record.getCreatedTime());
            dto.setStartTime(record.getStartTime());
            dto.setEndTime(record.getEndTime());

            // 获取计划名称
            Planv2 plan = planv2Service.getById(record.getPlanId());
            if (plan != null) {
                dto.setPlanName(plan.getName());
                // 判断是否有压测
                String loadId = plan.getLoadId();
                dto.setIsHaveLoad(loadId != null && !loadId.isEmpty() ? 1 : 0);
            }
            
            return dto;
        });

        return resultPage;
    }

    @Override
    public Map<Integer, Long> countByRecordStatus() {
        // 使用 selectCount 来获取分组计数
        List<Map<String, Object>> counts = this.baseMapper.selectMaps(
                new QueryWrapper<ChaosExerciseRecords>()
                        .select("record_status, COUNT(*) as count")
                        .groupBy("record_status"));

        // 转换结果
        return counts.stream()
                .collect(Collectors.toMap(
                        map -> ((Integer) map.get("record_status")),
                        map -> ((Long) map.get("count"))));
    }
}

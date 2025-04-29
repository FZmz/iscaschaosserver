package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Faultcorrelation;
import com.iscas.lndicatormonitor.domain.Statebound;
import com.iscas.lndicatormonitor.domain.faultconfigv2.ConfigTarget;
import com.iscas.lndicatormonitor.domain.faultconfigv2.FaultConfigNode;
import com.iscas.lndicatormonitor.domain.faultconfigv2.FaultConfigV2;
import com.iscas.lndicatormonitor.domain.faultconfigv2.Obeservedindexv2;
import com.iscas.lndicatormonitor.dto.FaultNodeSpecDTO;
import com.iscas.lndicatormonitor.dto.faultConfigv2.FaultConfigDTO;
import com.iscas.lndicatormonitor.dto.faultConfigv2.FaultConfigRecordItemDTO;
import com.iscas.lndicatormonitor.dto.faultConfigv2.FaultConfigv2QueryCriteria;
import com.iscas.lndicatormonitor.mapper.FaultConfigV2Mapper;
import com.iscas.lndicatormonitor.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
* @author mj
* @description 针对表【fault_config_v2】的数据库操作Service实现
* @createDate 2025-01-17 20:39:50
*/
@Slf4j
@Service
public class FaultConfigV2ServiceImpl extends ServiceImpl<FaultConfigV2Mapper, FaultConfigV2>
        implements FaultConfigV2Service {

    @Autowired
    private ConfigTargetService configTargetService;

    @Autowired
    private FaultConfigNodeService faultConfigNodeService;

    @Autowired
    private Obeservedindexv2Service obeservedindexv2Service;

    @Autowired
    private StateboundService stateboundService;

    @Autowired
    private FaultcorrelationService faultcorrelationService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomResult addFaultConfig(FaultConfigDTO faultConfigDTO) {
        try {
            // 1. 保存 FaultConfigV2
            FaultConfigV2 faultConfigV2 = faultConfigDTO.getFaultConfigV2();
            faultConfigV2.setCreateTime(new Date());
            faultConfigV2.setUpdateTime(new Date());
            faultConfigV2.setNodeTag(generateFaultTag());
            boolean saved = this.save(faultConfigV2);
            if (!saved) {
                throw new RuntimeException("保存故障配置基础信息失败");
            }
            Integer faultConfigId = faultConfigV2.getId();

            // 2. 保存 ConfigTarget
            ConfigTarget configTarget = faultConfigDTO.getConfigTarget();
            configTarget.setFaultConfigId(faultConfigId);
            boolean targetSaved = configTargetService.save(configTarget);
            if (!targetSaved) {
                throw new RuntimeException("保存配置目标信息失败");
            }

            // 3. 保存 ObservedIndexv2
            if (faultConfigDTO.getIndexesArr() != null && !faultConfigDTO.getIndexesArr().isEmpty()) {
                List<Obeservedindexv2> indexList = faultConfigDTO.getIndexesArr().stream()
                        .map(metricId -> {
                            Obeservedindexv2 index = new Obeservedindexv2();
                            index.setFaultConfigId(faultConfigId);
                            index.setMetricId(metricId);
                            return index;
                        })
                        .collect(Collectors.toList());
                boolean indexesSaved = obeservedindexv2Service.saveBatch(indexList);
                if (!indexesSaved) {
                    throw new RuntimeException("保存观测指标失败");
                }
            }

            // 4. 保存 FaultConfigNode
            if (faultConfigDTO.getFaultConfigNodes() != null && !faultConfigDTO.getFaultConfigNodes().isEmpty()) {
                List<FaultConfigNode> nodes = faultConfigDTO.getFaultConfigNodes();
                nodes.forEach(node -> node.setFaultConfigId(faultConfigId));
                boolean nodesSaved = faultConfigNodeService.saveBatch(nodes);
                if (!nodesSaved) {
                    throw new RuntimeException("保存故障节点配置失败");
                }
            }

            // 5. 保存 Statebound
            if (faultConfigDTO.getSteadyIdList() != null && !faultConfigDTO.getSteadyIdList().isEmpty()) {
                List<Statebound> statebounds = faultConfigDTO.getSteadyIdList().stream()
                        .map(steadyId -> {
                            Statebound statebound = new Statebound();
                            statebound.setId(UUID.randomUUID().toString()); // 生成唯一ID
                            statebound.setBoundType(1);
                            statebound.setBoundId(String.valueOf(faultConfigId));
                            statebound.setSteadyId(steadyId);
                            return statebound;
                        })
                        .collect(Collectors.toList());
                boolean stateboundsSaved = stateboundService.saveBatch(statebounds);
                if (!stateboundsSaved) {
                    throw new RuntimeException("保存稳态绑定失败");
                }
            }

            return CustomResult.ok(faultConfigId);

        } catch (Exception e) {
            log.error("添加故障配置失败", e);
            throw new RuntimeException("添加故障配置失败: " + e.getMessage());
        }
    }

    // 查询实现
    @Override
    public CustomResult getFaultConfigDetail(Integer faultConfigId) {
        try {
            // 1. 查询基本信息
            FaultConfigV2 faultConfigV2 = this.getById(faultConfigId);
            if (faultConfigV2 == null) {
                return CustomResult.fail("故障配置不存在");
            }

            // 2. 查询配置目标
            ConfigTarget configTarget = configTargetService.getOne(
                    new QueryWrapper<ConfigTarget>().eq("fault_config_id", faultConfigId)
            );

            // 3. 查询观测指标
            List<Obeservedindexv2> indexes = obeservedindexv2Service.list(
                    new QueryWrapper<Obeservedindexv2>().eq("fault_config_id", faultConfigId)
            );
            List<Integer> indexesArr = indexes.stream()
                    .map(Obeservedindexv2::getMetricId)
                    .collect(Collectors.toList());

            // 4. 查询故障节点
            List<FaultConfigNode> nodes = faultConfigNodeService.list(
                    new QueryWrapper<FaultConfigNode>().eq("fault_config_id", faultConfigId)
            );

            // 5. 查询稳态列表
            List<Statebound> statebounds = stateboundService.list(
                    new QueryWrapper<Statebound>()
                            .eq("bound_type", 1)
                            .eq("bound_id", faultConfigId.toString())
            );
            List<String> steadyIdList = statebounds.stream()
                    .map(Statebound::getSteadyId)
                    .collect(Collectors.toList());

            // 6. 组装返回数据
            FaultConfigDTO result = new FaultConfigDTO();
            result.setFaultConfigV2(faultConfigV2);
            result.setConfigTarget(configTarget);
            result.setIndexesArr(indexesArr);
            result.setFaultConfigNodes(nodes);
            result.setSteadyIdList(steadyIdList);

            return CustomResult.ok(result);

        } catch (Exception e) {
            log.error("查询故障配置详情失败, faultConfigId={}", faultConfigId, e);
            return CustomResult.fail("查询故障配置详情失败: " + e.getMessage());
        }
    }

    // 更新实现
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomResult updateFaultConfig(FaultConfigDTO faultConfigDTO) {
        try {
            // 1. 严格校验ID
            if (faultConfigDTO == null || faultConfigDTO.getFaultConfigV2() == null) {
                return CustomResult.fail("故障配置信息不能为空");
            }
            
            Integer faultConfigId = faultConfigDTO.getFaultConfigV2().getId();
            if (faultConfigId == null || faultConfigId <= 0) {
                return CustomResult.fail("故障配置ID不合法，无法执行更新操作");
            }
            
            // 2. 验证该ID的配置是否存在
            FaultConfigV2 existingConfig = this.getById(faultConfigId);
            if (existingConfig == null) {
                return CustomResult.fail("故障配置不存在，ID: " + faultConfigId);
            }

            // 3. 继续原有的更新逻辑
            FaultConfigV2 faultConfigV2 = faultConfigDTO.getFaultConfigV2();
            faultConfigV2.setUpdateTime(new Date());
            boolean updated = this.updateById(faultConfigV2);
            if (!updated) {
                throw new RuntimeException("更新故障配置基础信息失败");
            }

            // 4. 更新配置目标
            configTargetService.remove(
                    new QueryWrapper<ConfigTarget>().eq("fault_config_id", faultConfigId)
            );
            ConfigTarget configTarget = faultConfigDTO.getConfigTarget();
            configTarget.setFaultConfigId(faultConfigId);
            configTargetService.save(configTarget);

            // 5. 更新观测指标
            obeservedindexv2Service.remove(
                    new QueryWrapper<Obeservedindexv2>().eq("fault_config_id", faultConfigId)
            );
            if (faultConfigDTO.getIndexesArr() != null && !faultConfigDTO.getIndexesArr().isEmpty()) {
                List<Obeservedindexv2> indexList = faultConfigDTO.getIndexesArr().stream()
                        .map(metricId -> {
                            Obeservedindexv2 index = new Obeservedindexv2();
                            index.setFaultConfigId(faultConfigId);
                            index.setMetricId(metricId);
                            return index;
                        })
                        .collect(Collectors.toList());
                obeservedindexv2Service.saveBatch(indexList);
            }

            // 6. 更新故障节点
            faultConfigNodeService.remove(
                    new QueryWrapper<FaultConfigNode>().eq("fault_config_id", faultConfigId)
            );
            if (faultConfigDTO.getFaultConfigNodes() != null && !faultConfigDTO.getFaultConfigNodes().isEmpty()) {
                List<FaultConfigNode> nodes = faultConfigDTO.getFaultConfigNodes();
                nodes.forEach(node -> node.setFaultConfigId(faultConfigId));
                faultConfigNodeService.saveBatch(nodes);
            }

            // 7. 更新稳态绑定
            stateboundService.remove(
                    new QueryWrapper<Statebound>()
                            .eq("bound_type", 1)
                            .eq("bound_id", faultConfigId.toString())
            );
            if (faultConfigDTO.getSteadyIdList() != null && !faultConfigDTO.getSteadyIdList().isEmpty()) {
                List<Statebound> statebounds = faultConfigDTO.getSteadyIdList().stream()
                        .map(steadyId -> {
                            Statebound statebound = new Statebound();
                            statebound.setId(UUID.randomUUID().toString());
                            statebound.setBoundType(1);
                            statebound.setBoundId(faultConfigId.toString());
                            statebound.setSteadyId(steadyId);
                            return statebound;
                        })
                        .collect(Collectors.toList());
                stateboundService.saveBatch(statebounds);
            }

            return CustomResult.ok();
        } catch (Exception e) {
            log.error("更新故障配置失败", e);
            throw new RuntimeException("更新故障配置失败: " + e.getMessage());
        }
    }

    // 删除实现
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomResult deleteFaultConfig(Integer faultConfigId) {
        try {
            // 1. 检查是否存在
            if (!this.baseMapper.exists(new QueryWrapper<FaultConfigV2>().eq("id", faultConfigId))) {
                return CustomResult.fail("故障配置不存在");
            }
            // 2. 检查是否被计划引用
            if (faultcorrelationService.exists(
                    new QueryWrapper<Faultcorrelation>().eq("fault_config_id", faultConfigId)
            )) {
                return CustomResult.fail("该故障配置已被计划引用，无法删除");
            }

            // 3. 删除相关配置
            // 删除配置目标
            configTargetService.remove(
                    new QueryWrapper<ConfigTarget>().eq("fault_config_id", faultConfigId)
            );

            // 删除观测指标
            obeservedindexv2Service.remove(
                    new QueryWrapper<Obeservedindexv2>().eq("fault_config_id", faultConfigId)
            );

            // 删除故障节点
            faultConfigNodeService.remove(
                    new QueryWrapper<FaultConfigNode>().eq("fault_config_id", faultConfigId)
            );

            // 删除稳态绑定
            stateboundService.remove(
                    new QueryWrapper<Statebound>()
                            .eq("bound_type", 1)
                            .eq("bound_id", faultConfigId.toString())
            );

            // 最后删除故障配置本身
            this.removeById(faultConfigId);

            return CustomResult.ok();

        } catch (Exception e) {
            log.error("删除故障配置失败, faultConfigId={}", faultConfigId, e);
            throw new RuntimeException("删除故障配置失败: " + e.getMessage());
        }
    }

    @Override
    public CustomResult queryFaultConfigList(FaultConfigv2QueryCriteria criteria) {
        try {
            // 1. 参数校验和默认值设置
            if (criteria.getPageNum() == null || criteria.getPageNum() < 1) {
                criteria.setPageNum(1);
            }
            if (criteria.getPageSize() == null || criteria.getPageSize() < 1) {
                criteria.setPageSize(20);
            }

            // 2. 构建查询条件
            QueryWrapper<FaultConfigV2> queryWrapper = new QueryWrapper<>();

            // 名称模糊查询
            if (StringUtils.isNotBlank(criteria.getName())) {
                queryWrapper.like("name", criteria.getName());
            }

            // 创建者模糊查询
            if (StringUtils.isNotBlank(criteria.getCreatorName())) {
                queryWrapper.like("creator_name", criteria.getCreatorName());
            }

            // 目标类型精确查询
            if (criteria.getTargetType() != null) {
                queryWrapper.eq("target_type", criteria.getTargetType());
            }

            // 创建时间查询
            if (criteria.getCreateTime() != null) {
                queryWrapper.ge("create_time", criteria.getCreateTime());
            }

            // 更新时间查询
            if (criteria.getUpdateTime() != null) {
                queryWrapper.le("update_time", criteria.getUpdateTime());
            }

            // 排序处理
            if ("ASC".equalsIgnoreCase(criteria.getOrderByTime())) {
                queryWrapper.orderByAsc("create_time");
            } else {
                queryWrapper.orderByDesc("create_time");
            }

            // 3. 执行分页查询
            Page<FaultConfigV2> page = new Page<>(criteria.getPageNum(), criteria.getPageSize());
            Page<FaultConfigV2> faultConfigPage = this.page(page, queryWrapper);

            // 4. 转换并补充额外信息
            List<FaultConfigRecordItemDTO> resultList = new ArrayList<>();
            for (FaultConfigV2 config : faultConfigPage.getRecords()) {
                FaultConfigRecordItemDTO dto = new FaultConfigRecordItemDTO();

                // 基本信息复制
                dto.setId(config.getId());
                dto.setName(config.getName());
                dto.setTargetType(config.getTargetType());
                dto.setCreatorName(config.getCreatorName());
                dto.setCreateTime(config.getCreateTime());
                dto.setUpdateTime(config.getUpdateTime());

                // 查询关联稳态数量
                long steadyCount = stateboundService.count(
                        new QueryWrapper<Statebound>()
                                .eq("bound_type", 1)
                                .eq("bound_id", config.getId().toString())
                );
                dto.setSteadyCount((int)steadyCount);

                // 查询观测指标数量
                long indexCount = obeservedindexv2Service.count(
                        new QueryWrapper<Obeservedindexv2>()
                                .eq("fault_config_id", config.getId())
                );
                dto.setObservedIndexCount((int)indexCount);

                resultList.add(dto);
            }

            // 5. 构建返回结果
            Page<FaultConfigRecordItemDTO> resultPage = new Page<>(criteria.getPageNum(), criteria.getPageSize());
            resultPage.setRecords(resultList);
            resultPage.setTotal(faultConfigPage.getTotal());

            return CustomResult.ok(resultPage);

        } catch (Exception e) {
            log.error("查询故障配置列表失败", e);
            return CustomResult.fail("查询故障配置列表失败: " + e.getMessage());
        }
    }


    @Override
    public Page<FaultNodeSpecDTO> queryFaultConfigV2Node(FaultConfigv2QueryCriteria criteria) {
        LambdaQueryWrapper<FaultConfigV2> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.isNotBlank(criteria.getName()),
                        FaultConfigV2::getName, criteria.getName())
                .like(StringUtils.isNotBlank(criteria.getCreatorName()),
                        FaultConfigV2::getCreatorName, criteria.getCreatorName())
                .eq(criteria.getTargetType() != null,
                        FaultConfigV2::getTargetType, criteria.getTargetType());

        if ("ASC".equalsIgnoreCase(criteria.getOrderByTime())) {
            wrapper.orderByAsc(FaultConfigV2::getCreateTime);
        } else if ("DESC".equalsIgnoreCase(criteria.getOrderByTime())) {
            wrapper.orderByDesc(FaultConfigV2::getCreateTime);
        }

        Page<FaultConfigV2> page = new Page<>(criteria.getPageNum(), criteria.getPageSize());
        Page<FaultConfigV2> faultConfigPage = this.page(page, wrapper);

        try {
            List<FaultNodeSpecDTO> result = transformFaultConfig(faultConfigPage.getRecords());
            return new Page<FaultNodeSpecDTO>()
                    .setRecords(result)
                    .setTotal(faultConfigPage.getTotal())
                    .setSize(faultConfigPage.getSize())
                    .setCurrent(faultConfigPage.getCurrent());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Transform fault config failed", e);
        }
    }

    private List<FaultNodeSpecDTO> transformFaultConfig(List<FaultConfigV2> faultconfigList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<FaultNodeSpecDTO> faultNodeSpecDTOList = new ArrayList<>();
        for (FaultConfigV2 faultConfigV2 : faultconfigList){
            FaultNodeSpecDTO faultNodeSpecDTO = new FaultNodeSpecDTO();
            faultNodeSpecDTO.setId(faultConfigV2.getId());
            faultNodeSpecDTO.setFaultName(faultConfigV2.getName());
            faultNodeSpecDTO.setName(faultConfigV2.getNodeTag());
            faultNodeSpecDTO.setTemplateType("FaultConfig");
            faultNodeSpecDTO.setDeadline("1h");
            List<FaultConfigNode> faultConfignodeList = faultConfigNodeService.lambdaQuery()
                    .eq(FaultConfigNode::getFaultConfigId, faultConfigV2.getId())
                    .list();
            List<Object> innerNodeContentList = new ArrayList<>();
            for (FaultConfigNode faultConfigNode : faultConfignodeList){
                Object nodeContent = objectMapper.readValue(faultConfigNode.getContent(), Object.class);
                innerNodeContentList.add(nodeContent);
            }
            faultNodeSpecDTO.setList(innerNodeContentList);
            faultNodeSpecDTOList.add(faultNodeSpecDTO);
        }
        return faultNodeSpecDTOList;
    }
    public  String generateFaultTag() {
        // Generate a random UUID and extract the first 8 characters
        String randomPart = UUID.randomUUID().toString().substring(0, 8);
        // Combine "fault-" with the random part
        return "fault-" + randomPart;
    }
}
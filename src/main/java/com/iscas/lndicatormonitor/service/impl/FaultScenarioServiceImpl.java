package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.scenario.*;
import com.iscas.lndicatormonitor.mapper.FaultScenarioMapper;
import com.iscas.lndicatormonitor.mapper.ScenarioTagsCorrelationMapper;
import com.iscas.lndicatormonitor.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author mj
* @description 针对表【fault_scenario(故障场景表)】的数据库操作Service实现
* @createDate 2025-01-22 23:10:23
*/
@Service
@Slf4j
public class FaultScenarioServiceImpl extends ServiceImpl<FaultScenarioMapper, FaultScenario>
    implements FaultScenarioService {

    @Autowired
    private ScenarioTagsCorrelationService scenarioTagsCorrelationService;

    @Autowired
    private ScenarioTagsCorrelationMapper scenarioTagsCorrelationMapper;

    @Autowired
    private ScenarioCategoryService categoryService;

    @Autowired
    private ScenarioTagsService tagsService;

    @Autowired
    private Planv2Service planv2Service;

    @Autowired
    private ScenarioCategoryService scenarioCategoryService;

    @Override
    public boolean save(FaultScenario faultScenario) {
        faultScenario.setCreatedAt(new Date());
        faultScenario.setUpdatedAt(new Date());
        return super.save(faultScenario);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveWithTags(FaultScenario faultScenario) {
        // 保存场景基本信息
        faultScenario.setCreatedAt(new Date());
        faultScenario.setUpdatedAt(new Date());
        boolean saved = save(faultScenario);

        // 保存标签关联
        if (saved && faultScenario.getTagIds() != null) {
            faultScenario.getTagIds().forEach(tagId -> {
                ScenarioTagsCorrelation relation = new ScenarioTagsCorrelation();
                relation.setScenarioId(faultScenario.getId());
                relation.setTagsId(tagId);
                scenarioTagsCorrelationService.save(relation);
            });
        }
        return saved;
    }
    @Override
    public IPage<FaultScenario> queryFaultScenarios(FaultScenarioQueryCriteria criteria) {
        LambdaQueryWrapper<FaultScenario> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.isNotBlank(criteria.getName()),
                FaultScenario::getName, criteria.getName());

        wrapper.eq(criteria.getCategoryId() != null,
                FaultScenario::getCategoryId, criteria.getCategoryId());

        wrapper.eq(StringUtils.isNotBlank(criteria.getCreatorName()),
                FaultScenario::getCreatorName, criteria.getCreatorName());

        if (CollectionUtils.isNotEmpty(criteria.getTagIds())) {
            // 需要在 ScenarioTagsCorrelationMapper 中添加这个方法
            List<Integer> scenarioIds = scenarioTagsCorrelationMapper.getScenarioIdsByTagIds(criteria.getTagIds());
            if (!scenarioIds.isEmpty()) {
                wrapper.in(FaultScenario::getId, scenarioIds);
            } else {
                return new Page<>(criteria.getPageNum(), criteria.getPageSize());
            }
        }

        wrapper.orderBy(true, "DESC".equalsIgnoreCase(criteria.getOrderByTime()),
                FaultScenario::getCreatedAt);

        IPage<FaultScenario> page = this.page(
                new Page<>(criteria.getPageNum(), criteria.getPageSize()),
                wrapper
        );

        // 修正查询每个场景的标签
        page.getRecords().forEach(scenario -> {
            LambdaQueryWrapper<ScenarioTagsCorrelation> relationWrapper = new LambdaQueryWrapper<>();
            relationWrapper.eq(ScenarioTagsCorrelation::getScenarioId, scenario.getId());
            List<ScenarioTagsCorrelation> relations = scenarioTagsCorrelationMapper.selectList(relationWrapper);
            scenario.setTagIds(relations.stream()
                    .map(ScenarioTagsCorrelation::getTagsId)
                    .collect(Collectors.toList()));
        });

        return page;
    }
    @Override
    public FaultScenarioDTO getFaultScenarioDetail(Integer id) {
        // 获取故障场景信息
        FaultScenario faultScenario = this.getById(id);
        if (faultScenario == null) {
            throw new RuntimeException("故障场景不存在");
        }

        // 构建内容DTO
        FaultScenarioContentDTO contentDTO = new FaultScenarioContentDTO();
        contentDTO.setId(faultScenario.getId());
        contentDTO.setName(faultScenario.getName());
        contentDTO.setCreatorName(faultScenario.getCreatorName());
        contentDTO.setCreateTime(faultScenario.getCreatedAt());
        contentDTO.setUpdateTime(faultScenario.getUpdatedAt());
        contentDTO.setDescription(faultScenario.getDescription());
        // 获取分类名称
        ScenarioCategory category = categoryService.getById(faultScenario.getCategoryId());
        if (category != null) {
            contentDTO.setCategory(category.getCategoryName());
        }

        // 获取标签名称
        contentDTO.setTags(tagsService.getTagNamesByScenarioId(faultScenario.getId()));

        // 获取计划详情
        Object planDetail = null;
        if (faultScenario.getPlanId() != null) {
            planDetail = planv2Service.getPlanDetail(faultScenario.getPlanId()).getData();
        }

        // 构建返回对象
        FaultScenarioDTO dto = new FaultScenarioDTO();
        dto.setFaultScenarioContentDTO(contentDTO);
        dto.setPlanFaultScenarioDTO(planDetail);

        return dto;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithTags(FaultScenario faultScenario) {
        log.info("更新场景，ID: {}", faultScenario.getId());

        // 确认场景存在
        FaultScenario existing = getById(faultScenario.getId());
        if (existing == null) {
            throw new RuntimeException("场景不存在，ID: " + faultScenario.getId());
        }

        // 先更新主表
        faultScenario.setUpdatedAt(new Date());
        boolean updated = updateById(faultScenario);
        if (!updated) {
            throw new RuntimeException("更新场景失败");
        }

        // 删除旧关联
        LambdaQueryWrapper<ScenarioTagsCorrelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenarioTagsCorrelation::getScenarioId, faultScenario.getId());
        scenarioTagsCorrelationMapper.delete(wrapper);

        // 添加新关联，带验证
        if (CollectionUtils.isNotEmpty(faultScenario.getTagIds())) {
            for (Integer tagId : faultScenario.getTagIds()) {
                ScenarioTagsCorrelation relation = new ScenarioTagsCorrelation();
                relation.setScenarioId(faultScenario.getId());
                relation.setTagsId(tagId);

                // 再次验证场景ID
                FaultScenario validationCheck = getById(relation.getScenarioId());
                if (validationCheck == null) {
                    throw new RuntimeException("无法找到场景ID: " + relation.getScenarioId());
                }

                scenarioTagsCorrelationMapper.insert(relation);
            }
        }

        return true;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWithTags(Integer id) {
        FaultScenario scenario = getById(id);
        if (scenario == null) {
            throw new RuntimeException("故障场景不存在");
        }
        // 删除标签关联
        LambdaQueryWrapper<ScenarioTagsCorrelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenarioTagsCorrelation::getScenarioId, id);
        scenarioTagsCorrelationMapper.delete(wrapper);

        return removeById(id);
    }
}
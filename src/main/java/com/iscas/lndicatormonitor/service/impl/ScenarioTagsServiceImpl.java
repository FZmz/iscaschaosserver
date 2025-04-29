package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.scenario.ScenarioTags;
import com.iscas.lndicatormonitor.domain.scenario.ScenarioTagsCorrelation;
import com.iscas.lndicatormonitor.mapper.ScenarioTagsMapper;
import com.iscas.lndicatormonitor.service.ScenarioTagsCorrelationService;
import com.iscas.lndicatormonitor.service.ScenarioTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author mj
* @description 针对表【scenario_tags(场景标签表)】的数据库操作Service实现
* @createDate 2025-01-22 23:10:23
*/
@Service
public class ScenarioTagsServiceImpl extends ServiceImpl<ScenarioTagsMapper, ScenarioTags>
    implements ScenarioTagsService {

    @Autowired
    private ScenarioTagsCorrelationService correlationService;

    @Override
    public boolean save(ScenarioTags scenarioTags) {
        scenarioTags.setCreatedAt(new Date());
        scenarioTags.setUpdatedAt(new Date());
        return super.save(scenarioTags);
    }


    @Override
    public List<String> getTagNamesByScenarioId(Integer scenarioId) {
        List<ScenarioTagsCorrelation> correlations = correlationService.lambdaQuery()
                .eq(ScenarioTagsCorrelation::getScenarioId, scenarioId)
                .list();

        if(correlations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> tagIds = correlations.stream()
                .map(ScenarioTagsCorrelation::getTagsId)
                .collect(Collectors.toList());

        return this.listByIds(tagIds).stream()
                .map(ScenarioTags::getTagsName)
                .collect(Collectors.toList());
    }
}





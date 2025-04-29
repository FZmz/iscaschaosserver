package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.scenario.ScenarioTags;

import java.util.List;

/**
* @author mj
* @description 针对表【scenario_tags(场景标签表)】的数据库操作Service
* @createDate 2025-01-22 23:10:23
*/
public interface ScenarioTagsService extends IService<ScenarioTags> {
    List<String> getTagNamesByScenarioId(Integer scenarioId);
}

package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.scenario.ScenarioCategory;
import com.iscas.lndicatormonitor.mapper.ScenarioCategoryMapper;
import com.iscas.lndicatormonitor.service.ScenarioCategoryService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author mj
* @description 针对表【scenario_category(场景分类表)】的数据库操作Service实现
* @createDate 2025-01-22 23:10:23
*/
@Service
public class ScenarioCategoryServiceImpl extends ServiceImpl<ScenarioCategoryMapper, ScenarioCategory>
    implements ScenarioCategoryService {
    @Override
    public boolean save(ScenarioCategory scenarioCategory) {
        scenarioCategory.setCreatedAt(new Date());
        scenarioCategory.setUpdatedAt(new Date());
        return super.save(scenarioCategory);
    }
}





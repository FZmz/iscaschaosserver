package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.scenario.FaultScenario;
import com.iscas.lndicatormonitor.domain.scenario.FaultScenarioDTO;
import com.iscas.lndicatormonitor.domain.scenario.FaultScenarioQueryCriteria;

/**
* @author mj
* @description 针对表【fault_scenario(故障场景表)】的数据库操作Service
* @createDate 2025-01-22 23:10:23
*/
public interface FaultScenarioService extends IService<FaultScenario> {
    IPage<FaultScenario> queryFaultScenarios(FaultScenarioQueryCriteria criteria);
    FaultScenarioDTO getFaultScenarioDetail(Integer id);

    boolean saveWithTags(FaultScenario faultScenario);
    boolean updateWithTags(FaultScenario faultScenario);
    boolean deleteWithTags(Integer id);


}

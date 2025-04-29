package com.iscas.lndicatormonitor.domain.scenario;

import lombok.Data;

@Data
public class FaultScenarioDTO {
    private FaultScenarioContentDTO faultScenarioContentDTO;
    private Object planFaultScenarioDTO; // 使用Object，因为来自外部服务
}
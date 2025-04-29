package com.iscas.lndicatormonitor.domain.scenario;

import lombok.Data;

import java.util.List;

@Data
public class FaultScenarioQueryCriteria {
    private String name;
    private Integer categoryId;
    private List<Integer> tagIds;  // 改为List
    private String creatorName;
    private String orderByTime;
    private Integer pageNum;
    private Integer pageSize;
}
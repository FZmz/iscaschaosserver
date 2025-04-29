package com.iscas.lndicatormonitor.dto;

import com.iscas.lndicatormonitor.domain.Plan;
import com.iscas.lndicatormonitor.domain.Record;
import lombok.Data;

import java.util.List;

@Data
public class SinglePlanDTO {
    /*
    * 计划基本信息
    * */
    private PlanSpecDTO planSpecDTO;

    /*
    * 记录信息
    * */
    private List<RecordItemDTO> RecordList;

    private List<String> SteadyList;
}

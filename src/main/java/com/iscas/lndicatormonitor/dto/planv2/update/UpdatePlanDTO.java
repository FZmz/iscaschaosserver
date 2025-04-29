package com.iscas.lndicatormonitor.dto.planv2.update;

import com.iscas.lndicatormonitor.dto.WorkflowDTO;
import com.iscas.lndicatormonitor.dto.planv2.Planv2DTO;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePlanDTO {
    private Planv2DTO planv2DTO;
    private List<Integer> faultConfigIdList;
    private List<String> steadyIdList;
    private WorkflowDTO workflowDTO;
}
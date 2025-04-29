package com.iscas.lndicatormonitor.dto;

import com.iscas.lndicatormonitor.domain.Plan;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;

@Data
public class AddPlanDTO {
    
    private PlanDTO planDTO;
    private int[] faultConfigIdList;
    private WorkflowDTO workflowDTO;
    private List<String> steadyIdList;
}

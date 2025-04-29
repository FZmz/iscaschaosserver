package com.iscas.lndicatormonitor.dto.planv2;

import com.iscas.lndicatormonitor.dto.WorkflowDTO;
import lombok.Data;

import java.util.List;

@Data
public class AddPlanv2DTO {
    private Planv2DTO planv2DTO;
    private int[] faultConfigIdList;
    private WorkflowDTO workflowDTO;
    private List<String> steadyIdList;
}
package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class FaultConfigPhysicalDTO {
    private FaultConfigBasicInfo  basicInfo;
    private HashMap<String, String> addressDTO;
    private IndexesArrDTO indexesArrDTO;
    private List<FaultConfigNodes> faultConfigNodes;
    private List<String>  steadyIdList;
}

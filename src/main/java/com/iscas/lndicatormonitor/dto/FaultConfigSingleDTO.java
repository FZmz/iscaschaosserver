package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.List;

@Data
public class FaultConfigSingleDTO {
    private BasicInfo  basicInfo;
    private SelectorDTO selectorInfo;
    private AddressDTO addressInfo;
    private IndexesArrDTO indexesArrDTO;
    private List<FaultConfigNodes> faultConfigNodes;
    private List<String> steadyIdList;
}

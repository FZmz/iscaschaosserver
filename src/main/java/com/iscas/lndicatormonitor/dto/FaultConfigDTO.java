package com.iscas.lndicatormonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yukun Hou
 * @create 2023-10-11 15:14
 */

@Data
@NoArgsConstructor
public class FaultConfigDTO {
    private FaultConfigBasicInfo  basicInfo;
    private SelectorDTO selectorInfo;
    private IndexesArrDTO indexesArrDTO;
    private List<FaultConfigNodes> faultConfigNodes;
    private List<String> steadyIdList;
}
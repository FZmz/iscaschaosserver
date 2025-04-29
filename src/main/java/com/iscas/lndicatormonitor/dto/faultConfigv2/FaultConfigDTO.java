package com.iscas.lndicatormonitor.dto.faultConfigv2;

import com.iscas.lndicatormonitor.domain.faultconfigv2.ConfigTarget;
import com.iscas.lndicatormonitor.domain.faultconfigv2.FaultConfigNode;
import com.iscas.lndicatormonitor.domain.faultconfigv2.FaultConfigV2;
import lombok.Data;
import java.util.List;

@Data
public class FaultConfigDTO {
    private FaultConfigV2 faultConfigV2; // 基础配置信息
    private ConfigTarget configTarget;  // 配置目标（物理机或K8S）
    private List<Integer> indexesArr;      // 统一后的索引数组
    private List<FaultConfigNode> faultConfigNodes; // 故障节点配置
    private List<String> steadyIdList;     // 稳定性检查 ID 列表
}

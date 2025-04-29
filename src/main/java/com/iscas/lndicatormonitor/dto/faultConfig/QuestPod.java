package com.iscas.lndicatormonitor.dto.faultConfig;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QuestPod {
    private List<String> namespaces;
    private Map<String, String> labelSelectors;

}

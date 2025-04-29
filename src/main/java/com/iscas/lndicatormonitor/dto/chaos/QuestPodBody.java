package com.iscas.lndicatormonitor.dto.chaos;

import lombok.Data;

import java.util.List;

@Data
public class QuestPodBody {
    private List<String> namespaces;
    private Object labelSelectors;
}

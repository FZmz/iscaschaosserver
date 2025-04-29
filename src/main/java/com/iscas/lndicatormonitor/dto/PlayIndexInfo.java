package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlayIndexInfo {
    private String indexName;
    private List<Object> data;
}

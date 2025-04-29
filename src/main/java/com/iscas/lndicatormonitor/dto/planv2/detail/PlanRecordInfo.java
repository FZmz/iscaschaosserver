package com.iscas.lndicatormonitor.dto.planv2.detail;

import lombok.Data;

import java.util.List;

@Data
public class PlanRecordInfo {
    private List<RecordInfo> records;
    private long total;
    private long size;
    private long current;
    private long pages;
}
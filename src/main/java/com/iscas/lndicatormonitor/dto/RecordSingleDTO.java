package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecordSingleDTO {
    private RecordSpecDTO recordSpecDTO;
    private String graph;
    private List<FaultPlayInfo> faultPlayInfo;

    private Object faultConfigInfo;
}

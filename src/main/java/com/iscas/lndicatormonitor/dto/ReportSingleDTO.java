package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReportSingleDTO {
    private ReportSpecDTO reportSpecDTO;
    private ReportInfoDTO reportInfoDTO;
    private String graphData;
}

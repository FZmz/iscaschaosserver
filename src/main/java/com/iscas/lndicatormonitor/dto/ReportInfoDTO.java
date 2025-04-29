package com.iscas.lndicatormonitor.dto;

import lombok.Data;

@Data
public class ReportInfoDTO {
    /**
     * 报告结论
     */
    private String conclusion;

    /**
     * 报告问题
     */
    private String question;

    /**
     * 指定责任人
     */
    private String principal;

    /**
     * 报告建议
     */
    private String suggestion;
}

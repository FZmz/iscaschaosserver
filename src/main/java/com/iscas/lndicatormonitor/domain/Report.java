package com.iscas.lndicatormonitor.domain;

import java.util.Date;
import lombok.Data;

@Data
public class Report {
    /**
     * 报告id
     */
    private Integer id;

    /**
     * 关联演练记录id
     */
    private Integer recordId;

    /**
     * 报告结果(1: 成功 2:失败)
     */
    private Integer result;

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

    /**
     * 报告创建时间
     */
    private Date createTime;

    /**
     * 报告更新时间
     */
    private Date updateTime;

    /**
     * 报告名称
     */
    private String name;

    /**
     * 创建人id
     */
    private Integer creatorId;
}
package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RecordItemDTO {
    /**
     * 记录id
     */
    private Integer id;
    /**
     * 执行人员
     */
    private String player;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 执行状态(1: 运行中 2:成功 3:失败)
     */
    private Integer recordStatus;

    /**
     * 所属计划id
     */
    private String planName;

    /**
     * 故障列表
     */
    private List<String> faultNameList;

    private  int pressStatus;
}

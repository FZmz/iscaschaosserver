package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RecordDTO {
    /**
     * 记录id
     */
    private Integer id;

    /**
     * 压测状态(0: 无压测、1: 未开始 2:压测中 3:已结束)
     */
    private Integer pressStatus;

    /**
     * 演练进度 百分制
     */
    private Integer recordProgress;

    /**
     * 执行人员
     */
    private Integer playerId;

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
    private Integer planId;
}

package com.iscas.lndicatormonitor.dto.recordv2;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ChaosExeRecordsDTO {
    private int id; // recordId
    private String planName; // 演练计划名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime; // 创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime; // 开始时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime; // 结束时间
    private int recordStatus; // 演练状态 默认为0
    private String playerName; // 演练人名称
    private String testedAppName; // 被测应用名称
    private String expectedExeTime; // 预计执行时长
    private int faulInjectObjectNum; // 故障注入对象数
    private int faultNum; // 故障数量
    private List<Integer> faultConfigIdList;// 故障配置Id列表
    private int planId; // 演练计划id
    private int isHaveLoad; // 0 无压测 1 有压测
}

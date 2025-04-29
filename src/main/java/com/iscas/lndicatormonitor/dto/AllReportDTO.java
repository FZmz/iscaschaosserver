package com.iscas.lndicatormonitor.dto;

import java.time.LocalDateTime;

/**
 * @author Yukun Hou
 * @create 2023-10-12 15:04
 */
public class AllReportDTO {

    private  Integer id;

    private String faultIndex;

    private String creator;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer recordStatus;

    private Integer planId;

    private Integer pressStatus;

    private Integer recordProgress;

}

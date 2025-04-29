package com.iscas.lndicatormonitor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Linkrequest {
    private Integer id;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 请求内容
     */
    private String content;
}
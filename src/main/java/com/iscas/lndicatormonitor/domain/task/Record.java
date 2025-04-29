package com.iscas.lndicatormonitor.domain.task;

import lombok.Data;

import java.util.Date;

@Data
public class Record {
    private String id;
    private String task_id;
    private String executor;
    private Date start_time;
    private Date end_time;
    private int status;
}

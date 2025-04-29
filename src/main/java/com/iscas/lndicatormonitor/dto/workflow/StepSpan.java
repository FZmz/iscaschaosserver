package com.iscas.lndicatormonitor.dto.workflow;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class StepSpan {
    private String startTime;
    private String endTime;
}

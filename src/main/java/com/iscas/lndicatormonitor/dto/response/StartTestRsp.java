package com.iscas.lndicatormonitor.dto.response;

import lombok.Data;

@Data
public class StartTestRsp {
    private int status;
    private String msg;
    private String record_id;
    private Object warnings;
}

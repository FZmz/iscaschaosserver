package com.iscas.lndicatormonitor.dto.request;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class TestRequest {
    private ObjectId _id;
    private ObjectId record_id;
    private Integer code;
    private String question;
    private String suggestion;
    private String analysis;
    private long rsp_time;
    private ObjectId request_detail_id;
}

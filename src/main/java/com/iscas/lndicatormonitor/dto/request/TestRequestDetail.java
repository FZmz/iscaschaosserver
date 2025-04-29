package com.iscas.lndicatormonitor.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestRequestDetail {
    private String _id;
    private String request_id;
    private String fault_name;
    private String service_name;
    private RequestDetail request_detail;
    private ResponseDetail response_detail;

}

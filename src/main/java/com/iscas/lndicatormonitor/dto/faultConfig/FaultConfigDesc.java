package com.iscas.lndicatormonitor.dto.faultConfig;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Data
public class FaultConfigDesc {

    private Date createTime;
    private String creator;
    private String faultFirstType;
    private String namespace;
    private String address;
    private String podName;
}

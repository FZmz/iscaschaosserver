package com.iscas.lndicatormonitor.dto.faultConfig;

import lombok.Data;

@Data
public class Pod {
    private String ip;
    private String name;
    private String namespace;
    private String states;
}

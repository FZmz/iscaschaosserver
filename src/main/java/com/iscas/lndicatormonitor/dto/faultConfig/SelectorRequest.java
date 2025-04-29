package com.iscas.lndicatormonitor.dto.faultConfig;

import lombok.Data;

import java.util.Map;

@Data
public class SelectorRequest {
    private String namespace;
    private Map<String, String> selector;
}
package com.iscas.lndicatormonitor.dto;

import lombok.Data;

@Data
public class SelectorDTO {
    private String namespace;

    private String label;

    private String[] podNames;
}

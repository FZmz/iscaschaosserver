package com.iscas.lndicatormonitor.dto;

import lombok.Data;

@Data
public class PlayStatusInfo {
    private int unStart;
    private int running;
    private int success;
    private int failure;
}

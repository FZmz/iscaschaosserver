package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlayPodInfo {
    private String nameSpace;
    private String podName;

    private List<PlayIndexInfo> playIndexInfo;

    private List<Object> pressTestInfo;
}

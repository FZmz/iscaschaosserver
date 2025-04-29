package com.iscas.lndicatormonitor.dto;

import com.iscas.lndicatormonitor.dto.record.PlayHostInfo;
import lombok.Data;

import java.util.List;

@Data
public class FaultPlayInfo {
    private String paramInfo;
    private List<PlayPodInfo> playPodInfo;
    private List<PlayHostInfo> playHostInfos;
    private String name;
    private String graphData;
}

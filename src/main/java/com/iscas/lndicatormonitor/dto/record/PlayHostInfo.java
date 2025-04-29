package com.iscas.lndicatormonitor.dto.record;

import com.iscas.lndicatormonitor.dto.PlayIndexInfo;
import lombok.Data;

import java.util.List;

@Data
public class PlayHostInfo {
    private String host;
    private List<PlayIndexInfo> playIndexInfos;
    private List<Object> pressTestInfo;
}

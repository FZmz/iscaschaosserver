package com.iscas.lndicatormonitor.dto.planv2;

import lombok.Data;
import java.util.List;

@Data
public class TargetObjectNumDTO {
    private int objectNum;
    private List<String> podList;
    
    public TargetObjectNumDTO() {
    }
    
    public TargetObjectNumDTO(int objectNum) {
        this.objectNum = objectNum;
    }
} 
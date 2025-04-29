package com.iscas.lndicatormonitor.dto.planv2;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Planv2ListItemDTO {
    private int id;
    private String name;
    private int planType;
    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private int faultNum;
    private int SteadyNum;
}

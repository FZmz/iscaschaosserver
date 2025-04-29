package com.iscas.lndicatormonitor.domain.scenario;

import com.fasterxml.jackson.annotation.JsonFormat;import io.jsonwebtoken.lang.Strings;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FaultScenarioContentDTO {
    private Integer id;
    private String name;
    private String category;
    private List<String> tags;
    private String creatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String description;
}
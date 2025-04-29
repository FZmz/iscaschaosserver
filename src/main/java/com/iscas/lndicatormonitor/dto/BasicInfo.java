package com.iscas.lndicatormonitor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Yukun Hou
 * @create 2023-10-11 15:20
 */

@Data
@NoArgsConstructor
public class BasicInfo {
    private int id;
    private String name;
    private String creator;
    private Date createTime;
    private Date updateTime;
    private String graph;
    private String faultTypeConfig;
    /**
     * go端节点name
     * */
    private String nodeTag;
}

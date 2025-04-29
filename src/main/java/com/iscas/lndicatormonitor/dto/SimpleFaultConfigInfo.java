package com.iscas.lndicatormonitor.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SimpleFaultConfigInfo {
    /**
     * 演练故障id
     */
    private Integer id;

    /**
     * 故障演练名称
     */
    private String name;

    /**
     * 故障演练名称
     */
    private String nodeTag;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 观测节点
     */
    private IndexesArrDTO indexesArrDTO;
}

package com.iscas.lndicatormonitor.domain;

import java.util.Date;
import lombok.Data;

@Data
public class Faultconfig {
    /**
     * 演练故障id
     */
    private Integer id;

    /**
     * 故障演练名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人id
     */
    private Integer creatorId;

    /**
     * 故障配置流程图
     */
    private String graph;

    /**
     * 故障类别配置
     */
    private String faultTypeConfig;

    /**
    * go端节点name
    * */
    private String nodeTag;
}
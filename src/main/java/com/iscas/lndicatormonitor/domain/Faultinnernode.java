package com.iscas.lndicatormonitor.domain;

import lombok.Data;

@Data
public class Faultinnernode {
    /**
    * 流程节点id
    */
    private Integer id;

    /**
    * 所属故障id
    */
    private Integer faultConfigId;

    /**
    * 流程节点内容
    */
    private String content;

    /**
    * 流程节点序号
    */
    private Integer nodeIndex;

    /**
    * 流程节点状态（0:未执行 1:执行中 2:执行失败 3:执行成功）
    */
    private Integer nodeStatus;

    /**
    * 节点名称
    */
    private String name;

    /**
    * 节点类型
    */
    private String nodeType;
}
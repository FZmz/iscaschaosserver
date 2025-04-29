package com.iscas.lndicatormonitor.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yukun Hou
 * @create 2023-10-10 15:11
 */

@Data
@NoArgsConstructor
public class PrometheusIndex {

    @ApiModelProperty(value = "指标名")
    private String name;

    @ApiModelProperty(value = "命名空间")
    private String nameSpace;

    @ApiModelProperty(value = "Pod名称")
    private String podName;

    @ApiModelProperty(value = "开始时间")
    private double startTime;

    @ApiModelProperty(value = "结束时间")
    private double endTime;

    @ApiModelProperty(value = "查询间隔")
    private Integer step;


    @ApiModelProperty(value = "ip地址")
    private String ip;

}

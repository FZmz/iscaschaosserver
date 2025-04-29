package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.Result;
import com.iscas.lndicatormonitor.domain.PrometheusIndex;
import com.iscas.lndicatormonitor.service.PrometheusIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yukun Hou
 * @create 2023-10-10 15:11
 */

@RestController
@RequestMapping("PrometheusIndex")
public class PrometheusIndexController {

    @Autowired
    private PrometheusIndexService prometheusIndexService;

    @PostMapping("getPrometheusIndex")
    @OperationLog("获取Prometheus指标")
    public Result<Object> getPrometheusIndex(PrometheusIndex prometheusIndex) {
        try {
            return Result.success(prometheusIndexService.getPrometheusIndex(prometheusIndex), "查询成功");
        } catch (Exception e) {
            return Result.error("50000","请稍后查询......");
        }
    }


    @PostMapping("getNodeIndex")
    @OperationLog("获取节点指标")
    public Result<Object> getNodeIndex(PrometheusIndex prometheusIndex) {
        try {
            return Result.success(prometheusIndexService.getNodeIndex(prometheusIndex), "查询成功");
        } catch (Exception e) {
            return Result.error("50000","请稍后查询......");
        }
    }

}

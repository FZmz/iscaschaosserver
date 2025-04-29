package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Faultconfig;
import com.iscas.lndicatormonitor.domain.Faultinnernode;
import com.iscas.lndicatormonitor.domain.faultconfigv2.FaultConfigNode;
import com.iscas.lndicatormonitor.domain.faultconfigv2.FaultConfigV2;
import com.iscas.lndicatormonitor.dto.FaultConfigNodes;
import com.iscas.lndicatormonitor.dto.FaultNodeSpecDTO;
import com.iscas.lndicatormonitor.dto.faultConfigv2.FaultConfigDTO;
import com.iscas.lndicatormonitor.dto.faultConfigv2.FaultConfigv2QueryCriteria;
import com.iscas.lndicatormonitor.service.FaultConfigNodeService;
import com.iscas.lndicatormonitor.service.FaultConfigV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/faultConfigv2")
public class FaultConfigv2Controller {

    @Autowired
    private FaultConfigV2Service faultConfigV2Service;

    @Autowired
    private FaultConfigNodeService faultConfigNodeService;

//    @Autowired
//    private FaultConfig

    @PostMapping("/add")
    @OperationLog("新增故障配置")
    public CustomResult addFaultConfig(@RequestBody FaultConfigDTO faultConfigDTO) {
        return faultConfigV2Service.addFaultConfig(faultConfigDTO);
    }

    @GetMapping("/getDetail")
    @OperationLog("获取故障配置详情")
    public CustomResult getFaultConfigDetail(@RequestParam Integer faultConfigId) {
        return faultConfigV2Service.getFaultConfigDetail(faultConfigId);
    }

    @PutMapping("/update")
    @OperationLog("更新故障配置")
    public CustomResult updateFaultConfig(@RequestBody FaultConfigDTO faultConfigDTO) {
        return faultConfigV2Service.updateFaultConfig(faultConfigDTO);
    }

    @DeleteMapping("/delete")
    @OperationLog("删除故障配置")
    public CustomResult deleteFaultConfig(@RequestParam Integer faultConfigId) {
        return faultConfigV2Service.deleteFaultConfig(faultConfigId);
    }

    @PostMapping("/list")
    @OperationLog("获取故障配置列表")
    public CustomResult queryFaultConfigList(@RequestBody FaultConfigv2QueryCriteria criteria) {
        return faultConfigV2Service.queryFaultConfigList(criteria);
    }


    @PostMapping("/queryfaultConfigv2Node")
    @OperationLog("获取故障配置节点列表")
    public CustomResult queryfaultConfigv2Node(@RequestBody FaultConfigv2QueryCriteria criteria){
        Page<FaultNodeSpecDTO> result = faultConfigV2Service.queryFaultConfigV2Node(criteria);
        return CustomResult.ok(result);
    }
}
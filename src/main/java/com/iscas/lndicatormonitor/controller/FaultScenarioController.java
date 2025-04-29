package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.scenario.FaultScenario;
import com.iscas.lndicatormonitor.domain.scenario.FaultScenarioDTO;
import com.iscas.lndicatormonitor.domain.scenario.FaultScenarioQueryCriteria;
import com.iscas.lndicatormonitor.service.FaultScenarioService;
import com.iscas.lndicatormonitor.service.ScenarioCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/faultScenario")
public class FaultScenarioController {
    @Autowired
    private FaultScenarioService faultScenarioService;
    
    @Autowired
    private ScenarioCategoryService scenarioCategoryService;

    @PostMapping("/add")
    @OperationLog("新增故障场景")
    public CustomResult addFaultScenario(@RequestBody FaultScenario faultScenario) {
        try {
            boolean success = faultScenarioService.saveWithTags(faultScenario);
            return success ? CustomResult.ok() : CustomResult.fail("添加故障场景失败");
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }

    @GetMapping("/getDetail")
    @OperationLog("获取故障场景详情")
    public CustomResult getFaultScenario(@RequestParam int id) {
        try {
            FaultScenarioDTO faultScenario = faultScenarioService.getFaultScenarioDetail(id);
            return faultScenario != null ? CustomResult.ok(faultScenario) : CustomResult.fail("未找到故障场景");
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }

    @PostMapping("/list")
    @OperationLog("获取故障场景列表")
    public CustomResult listFaultScenarios(@RequestBody FaultScenarioQueryCriteria criteria) {
        try {
            IPage<FaultScenario> page = faultScenarioService.queryFaultScenarios(criteria);
            return CustomResult.ok(page);
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }

    @PutMapping("/update")
    @OperationLog("更新故障场景")
    public CustomResult updateFaultScenario(@RequestBody FaultScenario faultScenario) {
        try {
            boolean success = faultScenarioService.updateWithTags(faultScenario);
            return success ? CustomResult.ok() : CustomResult.fail("更新故障场景失败");
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }

    @DeleteMapping("/delete")
    @OperationLog("删除故障场景")
    public CustomResult deleteFaultScenario(@RequestParam int id) {
        try {
            boolean success = faultScenarioService.deleteWithTags(id);
            return success ? CustomResult.ok() : CustomResult.fail("删除故障场景失败");
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }
}

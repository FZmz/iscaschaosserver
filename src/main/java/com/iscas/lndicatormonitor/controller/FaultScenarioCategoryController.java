package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.scenario.ScenarioCategory;
import com.iscas.lndicatormonitor.service.ScenarioCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scenarioCategory")
public class FaultScenarioCategoryController {
    @Autowired
    private ScenarioCategoryService scenarioCategoryService;
    @PostMapping("/add")
    @OperationLog("新增场景分类")
    public CustomResult addScenarioCategory(@RequestBody ScenarioCategory scenarioCategory) {
        try {
            boolean success = scenarioCategoryService.save(scenarioCategory);
            return success ? CustomResult.ok() : CustomResult.fail("新增场景分类失败");
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }
    @GetMapping("/list")
    @OperationLog("获取场景分类列表")
    public CustomResult listScenarioCategories() {
        try {
            List<ScenarioCategory> categories = scenarioCategoryService.list();
            return CustomResult.ok(categories);
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }
}

package com.iscas.lndicatormonitor.controller;


import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.scenario.ScenarioTags;
import com.iscas.lndicatormonitor.service.ScenarioTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scenarioTag")
public class FaultScenarioTagController {

    @Autowired
    private ScenarioTagsService scenarioTagsService;

    @PostMapping("/add")
    public CustomResult addScenarioTag(@RequestBody ScenarioTags scenarioTags) {
        try {
            boolean success = scenarioTagsService.save(scenarioTags);
            return success ? CustomResult.ok() : CustomResult.fail("新增场景标签失败");
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }

    @GetMapping("/list")
    public CustomResult listScenarioTags() {
        try {
            List<ScenarioTags> tags = scenarioTagsService.list();
            return CustomResult.ok(tags);
        } catch (Exception e) {
            return CustomResult.fail(null);
        }
    }
}

package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Apidefinition;
import com.iscas.lndicatormonitor.service.ApidefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/apidefinition")
public class ApidefinitionController {

    @Autowired
    private ApidefinitionService apidefinitionService;

    // 1. 新增 API 定义
    @PostMapping("/add")
    @OperationLog("新增API定义")
    public CustomResult addApidefinition(@RequestBody Apidefinition apidefinition) {
        boolean result = apidefinitionService.save(apidefinition);
        return result ? CustomResult.ok(apidefinition.getId()) : CustomResult.fail("新增失败");
    }

    // 2. 根据 ID 删除 API 定义
    @DeleteMapping("/delete")
    @OperationLog("删除API定义")
    public CustomResult deleteApidefinition(@RequestParam String id) {
        boolean result = apidefinitionService.removeById(id);
        return result ? CustomResult.ok() : CustomResult.fail("删除失败");
    }

    // 3. 更新 API 定义信息
    @PutMapping("/update")
    @OperationLog("更新API定义")
    public CustomResult updateApidefinition(@RequestBody Apidefinition apidefinition) {
        try {
            boolean result = apidefinitionService.updateById(apidefinition);
            return result ? CustomResult.ok() : CustomResult.fail("更新失败");
        } catch (Exception e) {
            // Log the exception and return a failure message with details
            e.printStackTrace(); // Or use a logger like log.error() for better logging
            return CustomResult.fail("更新失败，错误信息: " + e.getMessage());
        }
    }


    // 4. 根据 ID 查询 API 定义详情
    @GetMapping("/get")
    @OperationLog("查询API定义详情")
    public CustomResult getApidefinitionById(@RequestParam String id) {
        Apidefinition apidefinition = apidefinitionService.getById(id);
        return apidefinition != null ? CustomResult.ok(apidefinition) : CustomResult.fail("未找到对应的 API 定义");
    }

    // 5. 分页查询 API 定义列表
    @GetMapping("/list")
    @OperationLog("分页查询API定义列表")
    public CustomResult listApidefinitions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Apidefinition> apidefinitionPage = new Page<>(page, size);
        IPage<Apidefinition> resultPage = apidefinitionService.page(apidefinitionPage);
        return CustomResult.ok(resultPage);
    }
}

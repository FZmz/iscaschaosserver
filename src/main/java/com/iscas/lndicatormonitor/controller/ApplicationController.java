package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Application;
import com.iscas.lndicatormonitor.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application")
public class ApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private ApplicationService applicationService;

    // 1. 新增应用
    @PostMapping("/add")
    @OperationLog("添加应用")
    public CustomResult addApplication(@RequestBody Application application) {
        try {
            return applicationService.addApplication(application);
        } catch (Exception e) {
            logger.error("添加应用失败", e);
            return CustomResult.fail("添加应用失败: " + null);
        }
    }

    // 2. 根据ID删除应用
    @DeleteMapping("/delete")
    @OperationLog("删除应用")
    public CustomResult deleteApplication(@RequestParam Long id) {
        try {
            boolean result = applicationService.removeById(id);
            return result ? CustomResult.ok() : CustomResult.fail("删除失败");
        } catch (Exception e) {
            logger.error("删除应用失败", e);
            return CustomResult.fail("删除应用失败: " + null);
        }
    }

    // 3. 更新应用信息
    @PutMapping("/update")
    @OperationLog("更新应用")
    public CustomResult updateApplication(@RequestBody Application application) {
        try {
            return applicationService.updateApplication(application);
        } catch (Exception e) {
            logger.error("更新应用失败", e);
            return CustomResult.fail("更新应用失败: " + null);
        }
    }

    // 4. 根据ID查询应用详情
    @GetMapping("/get")
    @OperationLog("查询应用详情")
    public CustomResult getApplicationById(@RequestParam Long id) {
        try {
            Application application = applicationService.getById(id);
            return application != null ? CustomResult.ok(application) : CustomResult.fail("未找到对应应用");
        } catch (Exception e) {
            logger.error("查询应用详情失败", e);
            return CustomResult.fail("查询应用详情失败: " + null);
        }
    }

    // 5. 分页查询应用列表
    @GetMapping("/list")
    @OperationLog("分页查询应用列表")
    public CustomResult listApplications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<Application> applicationPage = new Page<>(page, size);
            IPage<Application> resultPage = applicationService.page(applicationPage);
            return CustomResult.ok(resultPage);
        } catch (Exception e) {
            logger.error("分页查询应用列表失败", e);
            return CustomResult.fail("分页查询应用列表失败: " + null);
        }
    }

    @GetMapping("/getAll")
    @OperationLog("获取所有应用")
    public CustomResult getAllApplications() {
        try {
            return CustomResult.ok(applicationService.list());
        } catch (Exception e) {
            logger.error("获取所有应用失败", e);
            return CustomResult.fail("获取所有应用失败: " + null);
        }
    }
}

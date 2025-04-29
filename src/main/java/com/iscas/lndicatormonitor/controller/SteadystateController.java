package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Steadystate;
import com.iscas.lndicatormonitor.service.SteadystateService;
import com.iscas.lndicatormonitor.utils.SteadystateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/steadystate")
public class SteadystateController {

    private static final Logger logger = Logger.getLogger(CorootController.class);

    @Autowired
    private SteadystateService steadystateService;

    @Autowired
    private SteadystateUtils steadystateUtils;
    // 1. 新增稳态
    @PostMapping("/add")
    @OperationLog("新增稳态")
    public CustomResult addSteadystate(@RequestBody Steadystate steadystate) {
        return steadystateService.saveSteadystate(steadystate);
    }

    // 2. 根据ID删除稳态
    @DeleteMapping("/delete")
    @OperationLog("删除稳态")
    public CustomResult deleteSteadystate(@RequestParam String id) {
        boolean result = steadystateService.removeById(id);
        return result ? CustomResult.ok() : CustomResult.fail("删除失败");
    }

    // 3. 更新稳态信息
    @PutMapping("/update")
    @OperationLog("更新稳态信息")
    public CustomResult updateSteadystate(@RequestBody Steadystate steadystate) {
        try {
            boolean result = steadystateService.updateById(steadystate);
            return result ? CustomResult.ok() : CustomResult.fail("更新失败");
        } catch (Exception e) {
            // Log the exception and return a failure message with details
            e.printStackTrace(); // Or use a logger like log.error() for better logging
            return CustomResult.fail("更新失败，错误信息: " + null);
        }
    }

    // 4. 根据ID查询稳态详情
    @GetMapping("/get")
    @OperationLog("获取稳态详情")
    public CustomResult getSteadystateById(@RequestParam String id) {
        Steadystate steadystate = steadystateService.getById(id);
        return steadystate != null ? CustomResult.ok(steadystate) : CustomResult.fail("未找到对应稳态信息");
    }

    // 5. 分页查询稳态列表
    @GetMapping("/list")
    @OperationLog("获取稳态列表")
    public CustomResult listSteadystates(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Steadystate> steadystatePage = new Page<>(page, size);
        IPage<Steadystate> resultPage = steadystateService.page(steadystatePage);
        return CustomResult.ok(resultPage);
    }

    // 6. 根据 steadystateType 查询 steadystate
    @GetMapping("/listAllByType")
    @OperationLog("获取稳态列表")
    public CustomResult listAllByType(
            @RequestParam int type,
            @RequestParam int page,
            @RequestParam int size) {
        // 构造分页对象
        Page<Steadystate> pageObj = new Page<>(page, size);

        // 构造查询条件
        QueryWrapper<Steadystate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);

        // 分页查询
        Page<Steadystate> steadystatePage = new Page<>(page, size);
        IPage<Steadystate> resultPage = steadystateService.page(steadystatePage);

        // 返回分页结果
        return CustomResult.ok(resultPage);
    }
    /**
     * 根据 planId 获取稳态指标
     */
    @GetMapping("/metricsByPlan")
    @OperationLog("获取稳态指标")
    public CustomResult getMetricsByPlanId(
            @RequestParam("planId") String planId,
            @RequestParam(value = "from", required = false) Long from,
            @RequestParam(value = "to", required = false) Long to
    ) {
        if (from == null || to == null) {
            return CustomResult.ok(null);
        }
        return CustomResult.ok(steadystateService.getMetricsByPlanId(planId, from, to));
    }

    /**
     * 根据 faultConfigId 获取稳态指标
     */
    @GetMapping("/metricsByFaultConfig")
    @OperationLog("获取稳态指标")
    public CustomResult getMetricsByFaultConfigId(
            @RequestParam("faultConfigId") String faultConfigId,
            @RequestParam(value = "from", required = false) Long from,
            @RequestParam(value = "to", required = false) Long to
    ) {
        if (from == null || to == null) {
            return CustomResult.ok(null);
        }
        return CustomResult.ok(steadystateService.getMetricsByFaultConfigId(faultConfigId, from, to));
    }
    @GetMapping("/sayhello")
    @OperationLog("获取稳态指标")
    public String sayhello(){
        return steadystateUtils.sayhello();
    }
}

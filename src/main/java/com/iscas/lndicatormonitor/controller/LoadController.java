package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Load;
import com.iscas.lndicatormonitor.domain.LoadApi;
import com.iscas.lndicatormonitor.domain.LoadScript;
import com.iscas.lndicatormonitor.dto.load.LoadDTO;
import com.iscas.lndicatormonitor.service.LoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/load")
public class LoadController {

    @Autowired
    private LoadService loadService;

    // 1. 新增负载
    @PostMapping("/add")
    @OperationLog("新增负载")
    public CustomResult addLoad(@RequestBody LoadDTO loadDTO) {
        boolean result = loadService.saveLoadDefinition(loadDTO);
        return result ? CustomResult.ok(loadDTO.getId()) : CustomResult.fail("新增失败");
    }

    // 2. 根据ID删除负载
    @DeleteMapping("/delete")
    @OperationLog("删除负载")
    public CustomResult deleteLoad(@RequestParam String id) {
        boolean result = loadService.removeLoadAndRelatedDataById(id);
        return result ? CustomResult.ok() : CustomResult.fail("删除失败");
    }
    // 3. 更新负载信息
    @PutMapping("/update")
    @OperationLog("更新负载")
    public CustomResult updateLoad(@RequestBody LoadDTO loadDTO) {
        try {
            boolean result = loadService.updateLoadDefinition(loadDTO);
            return result ? CustomResult.ok() : CustomResult.fail("更新失败");
        } catch (Exception e) {
            // Log the exception and return a failure message with details
            e.printStackTrace(); // Or use a logger like log.error() for better logging
            return CustomResult.fail("更新失败 ");
        }
    }

//    // 6. 根据loadid查询loadscript详情
//    @GetMapping("/loadscript")
//    public CustomResult getLoadScriptsByLoadId(@RequestParam String loadId) {
//        List<LoadScript> loadScripts = loadService.getLoadScriptsByLoadId(loadId);
//        return CustomResult.ok(loadScripts);
//    }

    // 6. 根据 loadId 查询 loadScript 详情
    @GetMapping("/loadscript")
    @OperationLog("获取负载脚本详情")
    public CustomResult getLoadScriptsByLoadId(@RequestParam String loadId) {
        List<LoadScript> loadScripts = loadService.getLoadScriptsByLoadId(loadId);

        // 过滤掉已经逻辑删除的记录
        loadScripts = loadScripts.stream()
                .filter(script -> script.getIsDelete() == 0) // 只保留 isdelete 为 0 的记录
                .collect(Collectors.toList());

        return CustomResult.ok(loadScripts);
    }


//    // 7. 根据loadid查询loadapi详情
//    @GetMapping("/loadapi")
//    public CustomResult getLoadApisByLoadId(@RequestParam String loadId) {
//        List<LoadApi> loadApis = loadService.getLoadApisByLoadId(loadId);
//        return CustomResult.ok(loadApis);
//    }

    // 7. 根据 loadId 查询 loadApi 详情
    @GetMapping("/loadapi")
    @OperationLog("获取负载API详情")
    public CustomResult getLoadApisByLoadId(@RequestParam String loadId) {
        List<LoadApi> loadApis = loadService.getLoadApisByLoadId(loadId);

        // 过滤掉已经逻辑删除的记录
        loadApis = loadApis.stream()
                .filter(api -> api.getIsDelete() == 0) // 只保留 isdelete 为 0 的记录
                .collect(Collectors.toList());

        return CustomResult.ok(loadApis);
    }

//    // 8. 根据ID查询负载基本信息
//    @GetMapping("/get")
//    public CustomResult getLoadById(@RequestParam String id) {
//        Load load = loadService.getById(id);
//        return load != null ? CustomResult.ok(load) : CustomResult.fail("未找到对应负载信息");
//    }

    // 8. 根据 ID 查询负载基本信息
    @GetMapping("/get")
    @OperationLog("获取负载详情")
    public CustomResult getLoadById(@RequestParam String id) {
        Load load = loadService.getById(id);

        // 判断该负载是否被逻辑删除
        if (load != null && load.getIsDelete() == 0) {
            return CustomResult.ok(load);
        } else {
            return CustomResult.fail("未找到对应负载信息或负载已被删除");
        }
    }

//    // 9. 分页查询负载信息
//    @GetMapping("/list")
//    public CustomResult listLoads(
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer size) {
//        Page<Load> loadPage = new Page<>(page, size);
//        IPage<Load> resultPage = loadService.page(loadPage);
//        return CustomResult.ok(resultPage);
//    }

    // 9. 分页查询负载信息
    @GetMapping("/list")
    @OperationLog("获取负载列表")
    public CustomResult listLoads(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        // 创建分页对象
        Page<Load> loadPage = new Page<>(page, size);

        // 创建查询条件，增加 isdelete = 0 的过滤条件
        QueryWrapper<Load> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0); // 只查询 isdelete 字段为 0 的记录

        // 使用分页查询
        IPage<Load> resultPage = loadService.page(loadPage, queryWrapper);

        return CustomResult.ok(resultPage);
    }

}

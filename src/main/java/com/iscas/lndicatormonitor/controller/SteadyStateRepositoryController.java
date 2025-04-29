package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.SteadyStateRepository;
import com.iscas.lndicatormonitor.service.SteadyStateRepositoryService;
import com.iscas.lndicatormonitor.utils.SteadystateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/steadyStateRepository")
public class SteadyStateRepositoryController {
    @Autowired
    private SteadyStateRepositoryService stateRepositoryService;

    /**
     * 获取所有SteadyStateId
     */
    @GetMapping("/ids")
    @OperationLog("获取所有SteadyStateId")
    public CustomResult getAllIds() {
        try {
            List<Integer> ids = stateRepositoryService.list()
                    .stream()
                    .map(SteadyStateRepository::getId)
                    .collect(Collectors.toList());
            return CustomResult.ok(ids);
        } catch (Exception e) {
            return CustomResult.fail("获取ID列表失败: " + null);
        }
    }
    /**
     * 根据id获取详情信息
     */
    @GetMapping("/{id}")
    public CustomResult getById(@PathVariable Integer id) {
        try {
            SteadyStateRepository repository = stateRepositoryService.getById(id);
            if (repository == null) {
                return CustomResult.fail("未找到对应记录");
            }
            return CustomResult.ok(repository);
        } catch (Exception e) {
            return CustomResult.fail("获取详情失败: " + null);
        }
    }


    /**
     * 新增SteadyStateRepository
     */
    @PostMapping
    public CustomResult add(@RequestBody SteadyStateRepository repository) {
        try {
            if (stateRepositoryService.save(repository)) {
                return CustomResult.ok(repository);
            }
            return CustomResult.fail("新增失败");
        } catch (Exception e) {
            return CustomResult.fail("新增失败: " + null);
        }
    }
    /**
     * 根据id逻辑删除SteadyStateRepository
     */
    @DeleteMapping("/{id}")
    @OperationLog("删除SteadyStateRepository")
    public CustomResult deleteById(@PathVariable Integer id) {
        try {
            if (stateRepositoryService.removeById(id)) {
                return CustomResult.ok();
            }
            return CustomResult.fail("删除失败");
        } catch (Exception e) {
            return CustomResult.fail("删除失败: " + null);
        }
    }

    /**
     * 获取所有 SteadyStateRepository
     */
    @GetMapping("/all")
    @OperationLog("获取所有SteadyStateRepository")
    public CustomResult getAllRepositories() {
        try {
            List<SteadyStateRepository> repositories = stateRepositoryService.list();
            return CustomResult.ok(repositories);
        } catch (Exception e) {
            return CustomResult.fail("获取列表失败: " + null);
        }
    }
}

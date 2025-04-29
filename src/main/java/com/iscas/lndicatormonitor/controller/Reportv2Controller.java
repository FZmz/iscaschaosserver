package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.reportv2.ReportV2;
import com.iscas.lndicatormonitor.dto.reportv2.ReportQueryCriteria;
import com.iscas.lndicatormonitor.service.ReportV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/reportv2")
public class Reportv2Controller {

    @Autowired
    private ReportV2Service reportV2Service;

    @PostMapping("/add")
    @OperationLog("新增报告")
    public CustomResult addReport(@RequestBody ReportV2 report) {
        try {
            // 防止手动设置ID
            report.setId(null);
            
            boolean result = reportV2Service.addReport(report);
            if (result) {
                log.info("新增报告成功, ID: {}, 名称: {}", report.getId(), report.getName());
                return CustomResult.ok(report.getId());
            } else {
                return CustomResult.fail("新增报告失败");
            }
        } catch (Exception e) {
            log.error("新增报告失败", e);
            return CustomResult.fail("新增报告失败: " + null);
        }
    }

    @PostMapping("/list")
    @OperationLog("获取报告列表")
    public CustomResult queryReportList(@RequestBody ReportQueryCriteria criteria) {
        try {
            IPage<ReportV2> page = reportV2Service.queryReportList(criteria);
            return CustomResult.ok(page);
        } catch (Exception e) {
            log.error("查询报告列表失败", e);
            return CustomResult.fail("查询报告列表失败: " + null);
        }
    }

    @GetMapping("/detail/{id}")
    @OperationLog("获取报告详情")
    public CustomResult getReportDetail(@PathVariable Integer id) {
        try {
            ReportV2 report = reportV2Service.getReportDetail(id);
            return report != null ? CustomResult.ok(report) : CustomResult.fail("未找到对应的报告");
        } catch (Exception e) {
            log.error("获取报告详情失败", e);
            return CustomResult.fail("获取报告详情失败: " + null);
        }
    }

    @PutMapping("/update")
    @OperationLog("更新报告")
    public CustomResult updateReport(@RequestBody ReportV2 report) {
        try {
            boolean result = reportV2Service.updateReport(report);
            return result ? CustomResult.ok() : CustomResult.fail("更新报告失败");
        } catch (Exception e) {
            log.error("更新报告失败", e);
            return CustomResult.fail("更新报告失败: " + null);
        }
    }

    @DeleteMapping("/delete/{id}")
    @OperationLog("删除报告")
    public CustomResult deleteReport(@PathVariable Integer id) {
        try {
            boolean result = reportV2Service.deleteReport(id);
            return result ? CustomResult.ok() : CustomResult.fail("删除报告失败");
        } catch (Exception e) {
            log.error("删除报告失败", e);
            return CustomResult.fail("删除报告失败: " + null);
        }
    }
}

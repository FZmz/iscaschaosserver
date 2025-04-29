package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.*;
import com.iscas.lndicatormonitor.dto.*;
import com.iscas.lndicatormonitor.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    ReportService reportService;

    @Autowired
    UsersService usersService;
    @Autowired
    RecordService recordService;

    @Autowired
    PlanService planService;

    @Autowired
    FaultconfigService faultconfigService;
    @Autowired
    FaultcorrelationService faultcorrelationService;

    @Autowired
    SelectorService selectorService;

    @Autowired
    ObservedcorrelationService observedcorrelationService;

    @Autowired
    PrometheusIndexAnController prometheusIndexAnController;
    @PostMapping("/reportAdd")
    @OperationLog("新增报告")
    public CustomResult reportAdd(@RequestBody Report report){
        reportService.insert(report);
        return  CustomResult.ok();
    }
    private static final Logger logger = Logger.getLogger(ReportController.class);

    @GetMapping("/getReportsByPage")
    @OperationLog("获取报告列表")
    public CustomResult getReportsByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        logger.info("Received request for getReportsByPage with page=" + page + " and size=" + size);

        try {
            // 初始化分页对象
            Page<Report> pagination = new Page<>(page, size);
            logger.debug("Initialized Page object with page=" + page + ", size=" + size);

            // 分页查询报告
            Page<Report> reportPage = reportService.page(pagination);
            logger.info("Queried reportService.page, total records: " + reportPage.getTotal());

            List<Report> reportList = reportPage.getRecords();
            logger.info("Fetched " + reportList.size() + " records for the current page.");

            // 转换为 DTO 列表
            List<ReportDTO> reportDTOList = new ArrayList<>();
            for (Report report : reportList) {
                logger.debug("Processing report with ID: " + report.getId());

                ReportDTO reportDTO = new ReportDTO();
                BeanUtils.copyProperties(report, reportDTO);
                logger.debug("Copied properties from Report to ReportDTO for report ID: " + report.getId());

                // 获取计划名称
                String planName = planService.getPlanNameById(recordService.selectByPrimaryKey(report.getRecordId()).getPlanId());
                reportDTO.setPlanName(planName);
                logger.debug("Fetched plan name: " + planName + " for report ID: " + report.getId());

                // 获取创建者名称
                String creatorName = usersService.getRealNameById(report.getCreatorId());
                reportDTO.setCreator(creatorName);
                logger.debug("Fetched creator name: " + creatorName + " for report ID: " + report.getId());

                reportDTOList.add(reportDTO);
                logger.debug("Added ReportDTO for report ID: " + report.getId() + " to result list.");
            }

            // 封装分页结果
            Map<String, Object> result = new HashMap<>();
            result.put("total", reportPage.getTotal());
            result.put("data", reportDTOList);
            logger.info("Successfully prepared response with total records: " + reportPage.getTotal());

            return CustomResult.ok(result);

        } catch (Exception e) {
            logger.error("Error occurred while processing getReportsByPage", e);
            return new CustomResult(40000, "获取失败", null);
        }
    }
    @GetMapping("/getAllReport")
    @OperationLog("获取所有报告")
    public CustomResult getAllReport() throws Exception {
        List<Report> reportList = reportService.selectAll();
        List<ReportDTO> reportDTOList = new ArrayList<>();
        try {
            for (Report report: reportList){
                ReportDTO reportDTO = new ReportDTO();
                BeanUtils.copyProperties(report,reportDTO);
                reportDTO.setPlanName(planService.getPlanNameById(recordService.selectByPrimaryKey(report.getRecordId()).getPlanId()));
                reportDTO.setCreator(usersService.getRealNameById(report.getCreatorId()));
                reportDTOList.add(reportDTO);
            }
            return CustomResult.ok(reportDTOList);
        }catch (Exception e){
            return new CustomResult(40000,"获取失败",e);
        }
    }


    @PostMapping("/updateReport")
    @OperationLog("更新报告")
    public CustomResult updateReport(@RequestBody Report report){
        reportService.updateByPrimaryKeySelective(report);
        return CustomResult.ok();
    }
    @GetMapping("/getReportById")
    @OperationLog("获取报告详情")
    public CustomResult getReportById(int reportId) throws Exception {
        ReportSingleDTO reportSingleDTO = new ReportSingleDTO();
        ReportSpecDTO reportSpecDTO = new ReportSpecDTO();
        ReportInfoDTO reportInfoDTO = new ReportInfoDTO();
        List<FaultPlayInfo> faultPlayInfoList = new ArrayList<>();

        // 1、根据reportId查询report基本信息 可处理 ReportInfoDTO  还有reportSpecDTO的status
        Report report = reportService.selectByPrimaryKey(reportId);
        BeanUtils.copyProperties(report,reportInfoDTO);
        reportSpecDTO.setStatus(report.getResult());

        // 2、根据recordId 查询对应的record 可以获取到startTime与endTime operator duration
        Record record = recordService.selectByPrimaryKey(report.getRecordId());
        reportSpecDTO.setStartTime(record.getStartTime());
        reportSpecDTO.setEndTime(record.getEndTime());
        reportSpecDTO.setDuration(calculateDuration(record.getStartTime(),record.getEndTime()));
        reportSpecDTO.setOperator(usersService.getRealNameById(record.getPlayerId()));

        // 3、根据record 里对应的planId 可以查到plan 获取createTime updateTime
        Plan plan = planService.getPlanById(record.getPlanId());
        reportSpecDTO.setCreatTime(plan.getCreateTime());
        reportSpecDTO.setUpdateTime(plan.getUpdateTime());
        reportSpecDTO.setPlanName(plan.getName());

        // 4、处理graphData 从plan里得到 至此ReportInfoDTO 与reportSpecDTO还有graphData 处理完成
        reportSingleDTO.setReportSpecDTO(reportSpecDTO);
        reportSingleDTO.setReportInfoDTO(reportInfoDTO);
        reportSingleDTO.setGraphData(plan.getGraph());
        return CustomResult.ok(reportSingleDTO);
    }
    public  String calculateDuration(Date startTime, Date endTime) {
        if (endTime == null) {
            endTime = new Date(); // 如果 endTime 为 null，使用当前时间
        }
        long durationInSeconds = (endTime.getTime() - startTime.getTime()) / 1000;

        long hours = durationInSeconds / 3600;
        long minutes = (durationInSeconds % 3600) / 60;
        long seconds = durationInSeconds % 60;

        return hours + "h " + minutes + "min " + seconds + "s";
    }

    @GetMapping("/getReportInfo")
    @OperationLog("获取报告信息")
    public CustomResult getReportInfo(int reportId){
        return CustomResult.ok(reportService.selectByPrimaryKey(reportId));
    }

    @PostMapping("/delReport")
    @OperationLog("删除报告")
    public CustomResult delReport(int reportId){
        return CustomResult.ok(reportService.deleteByPrimaryKey(reportId));
    }
}

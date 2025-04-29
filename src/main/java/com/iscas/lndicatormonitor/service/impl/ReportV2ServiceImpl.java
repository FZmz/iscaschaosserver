package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.reportv2.ReportV2;
import com.iscas.lndicatormonitor.dto.reportv2.ReportQueryCriteria;
import com.iscas.lndicatormonitor.mapper.ReportV2Mapper;
import com.iscas.lndicatormonitor.service.ReportV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
* @author mj
* @description 针对表【report_v2】的数据库操作Service实现
* @createDate 2025-03-01 12:11:38
*/
@Slf4j
@Service
public class ReportV2ServiceImpl extends ServiceImpl<ReportV2Mapper, ReportV2>
    implements ReportV2Service {

    @Override
    public IPage<ReportV2> queryReportList(ReportQueryCriteria criteria) {
        log.info("开始查询报告列表, 查询条件: {}", criteria);
        
        LambdaQueryWrapper<ReportV2> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(criteria.getName())) {
            wrapper.like(ReportV2::getName, criteria.getName());
        }
        if (StringUtils.hasText(criteria.getCreator())) {
            wrapper.eq(ReportV2::getCreator, criteria.getCreator());
        }
        if (StringUtils.hasText(criteria.getStatus())) {
            wrapper.eq(ReportV2::getStatus, criteria.getStatus());
        }
        if (criteria.getRecordId() != null) {
            wrapper.eq(ReportV2::getChaosRecordId, criteria.getRecordId());
        }
        
        // 添加时间范围条件
        if (criteria.getStartTime() != null) {
            wrapper.ge(ReportV2::getCreatorTime, criteria.getStartTime());
        }
        if (criteria.getEndTime() != null) {
            wrapper.le(ReportV2::getCreatorTime, criteria.getEndTime());
        }
        
        // 设置排序
        if ("DESC".equalsIgnoreCase(criteria.getOrderByTime())) {
            wrapper.orderByDesc(ReportV2::getCreatorTime);
        } else {
            wrapper.orderByAsc(ReportV2::getCreatorTime);
        }
        
        // 执行分页查询
        Page<ReportV2> page = new Page<>(criteria.getPageNum(), criteria.getPageSize());
        IPage<ReportV2> result = this.page(page, wrapper);
        
        log.info("查询报告列表完成, 总记录数: {}", result.getTotal());
        return result;
    }

    @Override
    public boolean addReport(ReportV2 report) {
        log.info("开始新增报告, 报告名称: {}", report.getName());
        try {
            // 设置初始值
            report.setId(null);  // 确保不手动设置ID，让数据库自增
            report.setCreatorTime(new Date());
            report.setUpdateTime(new Date());
            if (report.getStatus() == null) {
                report.setStatus("0");
            }
            
            // 保存报告
            boolean result = this.save(report);
            
            if (result) {
                log.info("新增报告成功, 报告ID: {}, 名称: {}", report.getId(), report.getName());
            } else {
                log.error("新增报告失败, 报告名称: {}", report.getName());
            }
            
            return result;
        } catch (Exception e) {
            log.error("新增报告异常, 报告名称: {}, 错误: {}", report.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean updateReport(ReportV2 report) {
        log.info("开始更新报告, 报告ID: {}", report.getId());
        report.setUpdateTime(new Date());
        boolean result = this.updateById(report);
        log.info("更新报告{}, 报告ID: {}", result ? "成功" : "失败", report.getId());
        return result;
    }

    @Override
    public boolean deleteReport(Integer id) {
        log.info("开始删除报告, 报告ID: {}", id);
        boolean result = this.removeById(id);
        log.info("删除报告{}, 报告ID: {}", result ? "成功" : "失败", id);
        return result;
    }

    @Override
    public ReportV2 getReportDetail(Integer id) {
        log.info("开始获取报告详情, 报告ID: {}", id);
        ReportV2 report = this.getById(id);
        log.info("获取报告详情{}, 报告ID: {}", report != null ? "成功" : "失败", id);
        return report;
    }
}





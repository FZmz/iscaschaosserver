package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.reportv2.ReportV2;
import com.iscas.lndicatormonitor.dto.reportv2.ReportQueryCriteria;

/**
* @author mj
* @description 针对表【report_v2】的数据库操作Service
* @createDate 2025-03-01 12:11:38
*/
public interface ReportV2Service extends IService<ReportV2> {
    // 分页查询报告
    IPage<ReportV2> queryReportList(ReportQueryCriteria criteria);
    
    // 新增报告
    boolean addReport(ReportV2 report);
    
    // 更新报告
    boolean updateReport(ReportV2 report);
    
    // 删除报告
    boolean deleteReport(Integer id);
    
    // 获取报告详情
    ReportV2 getReportDetail(Integer id);
}

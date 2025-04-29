//package com.iscas.lndicatormonitor.controller;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.iscas.lndicatormonitor.common.CustomResult;
//import com.iscas.lndicatormonitor.domain.reportv2.ReportV2;
//import com.iscas.lndicatormonitor.dto.reportv2.ReportQueryCriteria;
//import com.iscas.lndicatormonitor.service.ReportV2Service;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class Reportv2ControllerTest {
//
//    @Mock
//    private ReportV2Service reportV2Service;
//
//    @InjectMocks
//    private Reportv2Controller reportv2Controller;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testAddReport() {
//        // 准备测试数据
//        ReportV2 report = new ReportV2();
//        report.setName("测试报告");
//        report.setChaosRecordId(1);
//        report.setCreator("admin");
//        report.setStatus("0");
//        report.setContent("测试内容");
//
//        // 模拟服务层返回
//        when(reportV2Service.addReport(any(ReportV2.class))).thenReturn(true);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.addReport(report);
//
//        // 验证结果
//        assertEquals(20000, result.getStatus());
//        verify(reportV2Service, times(1)).addReport(any(ReportV2.class));
//    }
//
//    @Test
//    void testQueryReportList() {
//        // 准备测试数据
//        ReportQueryCriteria criteria = new ReportQueryCriteria();
//        criteria.setName("测试");
//        criteria.setCreator("admin");
//        criteria.setStatus("0");
//        criteria.setPageNum(1);
//        criteria.setPageSize(10);
//
//        // 创建分页数据
//        Page<ReportV2> page = new Page<>(1, 10);
//        page.setRecords(new ArrayList<>());
//        page.setTotal(100);
//
//        // 模拟服务层返回
//        when(reportV2Service.queryReportList(any(ReportQueryCriteria.class))).thenReturn(page);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.queryReportList(criteria);
//
//        // 验证结果
//        assertEquals(20000, result.getStatus());
//        assertTrue(result.getData() instanceof IPage);
//        verify(reportV2Service, times(1)).queryReportList(any(ReportQueryCriteria.class));
//    }
//
//    @Test
//    void testGetReportDetail() {
//        // 准备测试数据
//        Integer reportId = 1;
//        ReportV2 report = new ReportV2();
//        report.setId(reportId);
//        report.setName("测试报告");
//        report.setContent("测试内容");
//
//        // 模拟服务层返回
//        when(reportV2Service.getReportDetail(reportId)).thenReturn(report);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.getReportDetail(reportId);
//
//        // 验证结果
//        assertEquals(20000, result.getStatus());
//        assertNotNull(result.getData());
//        verify(reportV2Service, times(1)).getReportDetail(reportId);
//    }
//
//    @Test
//    void testUpdateReport() {
//        // 准备测试数据
//        ReportV2 report = new ReportV2();
//        report.setId(1);
//        report.setName("更新后的报告");
//        report.setContent("更新后的内容");
//        report.setUpdateTime(new Date());
//
//        // 模拟服务层返回
//        when(reportV2Service.updateReport(any(ReportV2.class))).thenReturn(true);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.updateReport(report);
//
//        // 验证结果
//        assertEquals(20000, result.getStatus());
//        verify(reportV2Service, times(1)).updateReport(any(ReportV2.class));
//    }
//
//    @Test
//    void testDeleteReport() {
//        // 准备测试数据
//        Integer reportId = 1;
//
//        // 模拟服务层返回
//        when(reportV2Service.deleteReport(reportId)).thenReturn(true);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.deleteReport(reportId);
//
//        // 验证结果
//        assertEquals(20000, result.getStatus());
//        verify(reportV2Service, times(1)).deleteReport(reportId);
//    }
//
//    @Test
//    void testAddReportFail() {
//        // 准备测试数据
//        ReportV2 report = new ReportV2();
//
//        // 模拟服务层抛出异常
//        when(reportV2Service.addReport(any(ReportV2.class))).thenThrow(new RuntimeException("测试异常"));
//
//        // 执行测试
//        CustomResult result = reportv2Controller.addReport(report);
//
//        // 验证结果
//        assertEquals(50000, result.getStatus());
//        assertTrue(result.getMsg().contains("测试异常"));
//    }
//
//    @Test
//    void testQueryReportListFail() {
//        // 准备测试数据
//        ReportQueryCriteria criteria = new ReportQueryCriteria();
//
//        // 模拟服务层抛出异常
//        when(reportV2Service.queryReportList(any(ReportQueryCriteria.class)))
//            .thenThrow(new RuntimeException("查询异常"));
//
//        // 执行测试
//        CustomResult result = reportv2Controller.queryReportList(criteria);
//
//        // 验证结果
//        assertEquals(50000, result.getStatus());
//        assertTrue(result.getMsg().contains("查询报告列表失败"));
//    }
//
//    @Test
//    void testGetReportDetailFail() {
//        // 准备测试数据
//        Integer reportId = 1;
//
//        // 模拟服务层返回null
//        when(reportV2Service.getReportDetail(reportId)).thenReturn(null);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.getReportDetail(reportId);
//
//        // 验证结果
//        assertEquals(50000, result.getStatus());
//        assertEquals("未找到对应的报告", result.getMsg());
//    }
//
//    @Test
//    void testGetReportDetailError() {
//        // 准备测试数据
//        Integer reportId = 1;
//
//        // 模拟服务层抛出异常
//        when(reportV2Service.getReportDetail(reportId))
//            .thenThrow(new RuntimeException("获取详情异常"));
//
//        // 执行测试
//        CustomResult result = reportv2Controller.getReportDetail(reportId);
//
//        // 验证结果
//        assertEquals(50000, result.getStatus());
//        assertTrue(result.getMsg().contains("获取报告详情失败"));
//    }
//
//    @Test
//    void testUpdateReportFail() {
//        // 准备测试数据
//        ReportV2 report = new ReportV2();
//
//        // 模拟服务层返回false
//        when(reportV2Service.updateReport(any(ReportV2.class))).thenReturn(false);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.updateReport(report);
//
//        // 验证结果
//        assertEquals(50000, result.getStatus());
//        assertEquals("更新报告失败", result.getMsg());
//    }
//
//    @Test
//    void testDeleteReportFail() {
//        // 准备测试数据
//        Integer reportId = 1;
//
//        // 模拟服务层抛出异常
//        when(reportV2Service.deleteReport(reportId))
//            .thenThrow(new RuntimeException("删除异常"));
//
//        // 执行测试
//        CustomResult result = reportv2Controller.deleteReport(reportId);
//
//        // 验证结果
//        assertEquals(50000, result.getStatus());
//        assertTrue(result.getMsg().contains("删除报告失败"));
//    }
//
//    @Test
//    void testAddReportSuccess() {
//        // 准备测试数据
//        ReportV2 report = new ReportV2();
//        report.setName("性能测试报告");
//        report.setChaosRecordId(1);
//        report.setCreator("admin");
//        report.setStatus("0");
//        report.setContent("这是一份完整的性能测试报告内容");
//
//        // 模拟服务层返回
//        when(reportV2Service.addReport(any(ReportV2.class))).thenReturn(true);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.addReport(report);
//
//        // 验证结果
//        assertEquals(20000, result.getStatus());
//        assertEquals("OK", result.getMsg());
//        verify(reportV2Service, times(1)).addReport(any(ReportV2.class));
//    }
//
//    @Test
//    void testQueryReportListSuccess() {
//        // 准备测试数据
//        ReportQueryCriteria criteria = new ReportQueryCriteria();
//        criteria.setName("性能测试");
//        criteria.setCreator("admin");
//        criteria.setStatus("0");
//        criteria.setOrderByTime("DESC");
//        criteria.setPageNum(1);
//        criteria.setPageSize(10);
//
//        // 创建测试报告列表
//        List<ReportV2> reportList = new ArrayList<>();
//        ReportV2 report1 = new ReportV2();
//        report1.setId(1);
//        report1.setName("性能测试报告1");
//        report1.setCreator("admin");
//        report1.setCreatorTime(new Date());
//        reportList.add(report1);
//
//        ReportV2 report2 = new ReportV2();
//        report2.setId(2);
//        report2.setName("性能测试报告2");
//        report2.setCreator("admin");
//        report2.setCreatorTime(new Date());
//        reportList.add(report2);
//
//        // 创建分页数据
//        Page<ReportV2> page = new Page<>(1, 10);
//        page.setRecords(reportList);
//        page.setTotal(2);
//
//        // 模拟服务层返回
//        when(reportV2Service.queryReportList(any(ReportQueryCriteria.class))).thenReturn(page);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.queryReportList(criteria);
//
//        // 验证结果
//        assertEquals(20000, result.getStatus());
//        assertTrue(result.getData() instanceof IPage);
//        IPage<ReportV2> resultPage = (IPage<ReportV2>) result.getData();
//        assertEquals(2, resultPage.getTotal());
//        assertEquals(2, resultPage.getRecords().size());
//        verify(reportV2Service, times(1)).queryReportList(any(ReportQueryCriteria.class));
//    }
//
//    @Test
//    void testGetReportDetailSuccess() {
//        // 准备测试数据
//        Integer reportId = 1;
//        ReportV2 report = new ReportV2();
//        report.setId(reportId);
//        report.setName("详细性能测试报告");
//        report.setCreator("admin");
//        report.setCreatorTime(new Date());
//        report.setContent("这是一份包含完整测试数据的性能测试报告");
//        report.setStatus("0");
//        report.setChaosRecordId(100);
//
//        // 模拟服务层返回
//        when(reportV2Service.getReportDetail(reportId)).thenReturn(report);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.getReportDetail(reportId);
//
//        // 验证结果
//        assertEquals(20000, result.getStatus());
//        assertNotNull(result.getData());
//        ReportV2 resultReport = (ReportV2) result.getData();
//        assertEquals("详细性能测试报告", resultReport.getName());
//        assertEquals("admin", resultReport.getCreator());
//        verify(reportV2Service, times(1)).getReportDetail(reportId);
//    }
//
//    @Test
//    void testUpdateReportSuccess() {
//        // 准备测试数据
//        ReportV2 report = new ReportV2();
//        report.setId(1);
//        report.setName("更新后的性能测试报告");
//        report.setContent("更新后的测试报告内容，包含最新的测试数据");
//        report.setUpdateTime(new Date());
//        report.setStatus("1");
//
//        // 模拟服务层返回
//        when(reportV2Service.updateReport(any(ReportV2.class))).thenReturn(true);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.updateReport(report);
//
//        // 验证结果
//        assertEquals(20000, result.getStatus());
//        assertEquals("success", result.getMsg());
//        verify(reportV2Service, times(1)).updateReport(any(ReportV2.class));
//    }
//
//    @Test
//    void testDeleteReportSuccess() {
//        // 准备测试数据
//        Integer reportId = 1;
//
//        // 模拟服务层返回
//        when(reportV2Service.deleteReport(reportId)).thenReturn(true);
//
//        // 执行测试
//        CustomResult result = reportv2Controller.deleteReport(reportId);
//
//        // 验证结果
//        assertEquals(20000, result.getStatus());
//        assertEquals("success", result.getMsg());
//        verify(reportV2Service, times(1)).deleteReport(reportId);
//    }
//}
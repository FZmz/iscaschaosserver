//package com.iscas.lndicatormonitor.mysql;
//
//import com.iscas.lndicatormonitor.domain.Workflowcorrelation;
//import com.iscas.lndicatormonitor.service.WorkflowcorrelationService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import org.junit.jupiter.api.Order;
//import lombok.extern.slf4j.Slf4j;
//import java.util.Arrays;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//@Slf4j
//@SpringBootTest
//public class WorkflowcorrelationServiceTest {
//
//    @Autowired
//    private WorkflowcorrelationService workflowcorrelationService;
//
//    @Test
//    @Order(1)
//    public void testInsert() {
//        Workflowcorrelation correlation = new Workflowcorrelation();
//        correlation.setName("test-workflow-1");
//        correlation.setWorkflowId(1001);
//        correlation.setRecordId(2001);
//
//        boolean result = workflowcorrelationService.save(correlation);
//        assertTrue(result);
//        System.out.println(correlation);
//        assertNotNull(correlation.getId());
//        log.info("Inserted workflow correlation with ID: {}", correlation.getId());
//    }
//
//    @Test
//    @Order(2)
//    public void testBatchInsert() {
//        List<Workflowcorrelation> correlations = Arrays.asList(
//                createWorkflowCorrelation("test-workflow-2", 1002, 2002),
//                createWorkflowCorrelation("test-workflow-3", 1003, 2003),
//                createWorkflowCorrelation("test-workflow-4", 1004, 2004)
//        );
//
//        boolean result = workflowcorrelationService.saveBatch(correlations);
//        assertTrue(result);
//        correlations.forEach(c -> assertNotNull(c.getId()));
//        log.info("Batch inserted {} records", correlations.size());
//    }
//
//    @Test
//    @Order(3)
//    public void testQueryByWorkflowName() {
//        // 根据工作流名称查询
//        LambdaQueryWrapper<Workflowcorrelation> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Workflowcorrelation::getName, "test-workflow-1");
//
//        Workflowcorrelation correlation = workflowcorrelationService.getOne(wrapper);
//        assertNotNull(correlation);
//        assertEquals("test-workflow-1", correlation.getName());
//        log.info("Found workflow: {}", correlation.getName());
//    }
//
//    @Test
//    @Order(4)
//    public void testQueryByRecordId() {
//        // 根据记录ID查询
//        LambdaQueryWrapper<Workflowcorrelation> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Workflowcorrelation::getRecordId, 2001);
//
//        Workflowcorrelation correlation = workflowcorrelationService.getOne(wrapper);
//        assertNotNull(correlation);
//        assertEquals(2001, correlation.getRecordId());
//        log.info("Found correlation for record ID: {}", correlation.getRecordId());
//    }
//
//    @Test
//    @Order(5)
//    public void testPageQuery() {
//        // 分页查询
//        Page<Workflowcorrelation> page = new Page<>(1, 2);
//        LambdaQueryWrapper<Workflowcorrelation> wrapper = new LambdaQueryWrapper<>();
//        wrapper.orderByDesc(Workflowcorrelation::getId);
//
//        Page<Workflowcorrelation> resultPage = workflowcorrelationService.page(page, wrapper);
//
//        assertNotNull(resultPage);
//        assertNotNull(resultPage.getRecords());
//        assertEquals(2, resultPage.getSize());
//        log.info("Page query total: {}, current page size: {}",
//                resultPage.getTotal(), resultPage.getRecords().size());
//    }
//
//    @Test
//    @Order(6)
//    public void testUpdate() {
//        // 先查询一条记录
//        LambdaQueryWrapper<Workflowcorrelation> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Workflowcorrelation::getName, "test-workflow-1");
//
//        Workflowcorrelation correlation = workflowcorrelationService.getOne(wrapper);
//        assertNotNull(correlation);
//
//        // 更新工作流名称
//        String newName = "test-workflow-1-updated";
//        correlation.setName(newName);
//        boolean result = workflowcorrelationService.updateById(correlation);
//        assertTrue(result);
//
//        // 验证更新
//        Workflowcorrelation updated = workflowcorrelationService.getById(correlation.getId());
//        assertEquals(newName, updated.getName());
//        log.info("Updated workflow name to: {}", updated.getName());
//    }
//
//    @Test
//    @Order(7)
//    public void testDelete() {
//        // 先查询一条记录
//        LambdaQueryWrapper<Workflowcorrelation> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Workflowcorrelation::getName, "test-workflow-2");
//
//        Workflowcorrelation correlation = workflowcorrelationService.getOne(wrapper);
//        assertNotNull(correlation);
//
//        // 删除记录
//        boolean result = workflowcorrelationService.removeById(correlation.getId());
//        assertTrue(result);
//
//        // 验证删除
//        Workflowcorrelation deleted = workflowcorrelationService.getById(correlation.getId());
//        assertNull(deleted);
//        log.info("Successfully deleted workflow correlation with ID: {}", correlation.getId());
//    }
//
//    private Workflowcorrelation createWorkflowCorrelation(String name, Integer workflowId, Integer recordId) {
//        Workflowcorrelation correlation = new Workflowcorrelation();
//        correlation.setName(name);
//        correlation.setWorkflowId(workflowId);
//        correlation.setRecordId(recordId);
//        return correlation;
//    }
//
//    @Test
//    @Order(8)
//    public void testQueryByMultipleConditions() {
//        LambdaQueryWrapper<Workflowcorrelation> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Workflowcorrelation::getWorkflowId, 1003)
//                .eq(Workflowcorrelation::getRecordId, 2003);
//
//        Workflowcorrelation correlation = workflowcorrelationService.getOne(wrapper);
//        assertNotNull(correlation);
//        assertEquals("test-workflow-3", correlation.getName());
//        log.info("Found correlation by multiple conditions: {}", correlation.getName());
//    }
//}
//package com.iscas.lndicatormonitor.mysql;
//
//import com.iscas.lndicatormonitor.domain.LoadCorrelation;
//import com.iscas.lndicatormonitor.service.LoadCorrelationService;
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
//public class LoadCorrelationServiceTest {
//
//    @Autowired
//    private LoadCorrelationService loadCorrelationService;
//
//    @Test
//    @Order(1)
//    public void testInsert() {
//        LoadCorrelation correlation = new LoadCorrelation();
//        correlation.setLoadTaskId("test-task-001");
//        correlation.setChaosExerciseRecordId(1);
//        boolean result = loadCorrelationService.save(correlation);
//        assertTrue(result);
//        assertNotNull(correlation.getId());
//        log.info("Inserted record with ID: {}", correlation.getId());
//    }
//
//    @Test
//    @Order(2)
//    public void testBatchInsert() {
//        List<LoadCorrelation> correlations = Arrays.asList(
//                createCorrelation("test-task-002", 2),
//                createCorrelation("test-task-003", 3),
//                createCorrelation("test-task-004", 4)
//        );
//
//        boolean result = loadCorrelationService.saveBatch(correlations);
//        assertTrue(result);
//        correlations.forEach(c -> assertNotNull(c.getId()));
//    }
//
//    @Test
//    @Order(3)
//    public void testQuery() {
//        // 按条件查询
//        LambdaQueryWrapper<LoadCorrelation> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(LoadCorrelation::getLoadTaskId, "test-task-001");
//
//        LoadCorrelation correlation = loadCorrelationService.getOne(wrapper);
//        assertNotNull(correlation);
//        assertEquals("test-task-001", correlation.getLoadTaskId());
//    }
//
//    @Test
//    @Order(4)
//    public void testPageQuery() {
//        // 分页查询
//        Page<LoadCorrelation> page = new Page<>(1, 2);
//        Page<LoadCorrelation> resultPage = loadCorrelationService.page(page);
//
//        assertNotNull(resultPage);
//        assertNotNull(resultPage.getRecords());
//        assertEquals(2, resultPage.getSize());
//        log.info("Total records: {}", resultPage.getTotal());
//    }
//
//    @Test
//    @Order(5)
//    public void testUpdate() {
//        // 先查询一条记录
//        LambdaQueryWrapper<LoadCorrelation> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(LoadCorrelation::getLoadTaskId, "test-task-001");
//
//        LoadCorrelation correlation = loadCorrelationService.getOne(wrapper);
//        assertNotNull(correlation);
//
//        // 更新记录
//        correlation.setLoadTaskId("test-task-001-updated");
//        boolean result = loadCorrelationService.updateById(correlation);
//        assertTrue(result);
//
//        // 验证更新
//        LoadCorrelation updated = loadCorrelationService.getById(correlation.getId());
//        assertEquals("test-task-001-updated", updated.getLoadTaskId());
//    }
//
//    @Test
//    @Order(6)
//    public void testDelete() {
//        // 先查询一条记录
//        LambdaQueryWrapper<LoadCorrelation> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(LoadCorrelation::getLoadTaskId, "test-task-002");
//
//        LoadCorrelation correlation = loadCorrelationService.getOne(wrapper);
//        assertNotNull(correlation);
//
//        // 删除记录
//        boolean result = loadCorrelationService.removeById(correlation.getId());
//        assertTrue(result);
//
//        // 验证删除
//        LoadCorrelation deleted = loadCorrelationService.getById(correlation.getId());
//        assertNull(deleted);
//    }
//
//    private LoadCorrelation createCorrelation(String loadTaskId, Integer recordId) {
//        LoadCorrelation correlation = new LoadCorrelation();
//        correlation.setLoadTaskId(loadTaskId);
//        correlation.setChaosExerciseRecordId(recordId);
//        return correlation;
//    }
//}
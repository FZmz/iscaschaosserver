//package com.iscas.lndicatormonitor.PrometheusProcessorTest;
//
//import com.iscas.lndicatormonitor.dto.indexRecommend.PrometheusProcessorDTO;
//import com.iscas.lndicatormonitor.rule.MetricQueryRuleLibrary;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.nio.charset.StandardCharsets;
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.Map;
//
//@SpringBootTest
//public class PrometheusQueryTest {
//    @Autowired
//    private MetricQueryRuleLibrary ruleLibrary;
//    private PrometheusProcessorDTO dto;
//
//    @BeforeEach
//    void setUp() {
//        dto = new PrometheusProcessorDTO();
//        dto.setIndexName("node_resources_disk_read_bytes_total");
//        dto.setInstance("1");
//        dto.setFrom(1735865408628L);
//        dto.setTo(1735895408628L);
//    }
//
//    @Test
//    void testQueryGeneration() {
//        try {
//            // 准备测试数据
//            Map<String, String> params = new HashMap<>();
//            params.put("instance", dto.getInstance());
//
//            // 测试查询生成
//            String query = ruleLibrary.getQuery(dto.getIndexName(), params);
//            System.out.println("Generated Query: " + query);
//            assertNotNull(query, "Generated query should not be null");
//
//            // 测试URL编码
//            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
//            System.out.println("Encoded Query: " + encodedQuery);
//            assertNotNull(encodedQuery, "Encoded query should not be null");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Test failed with exception: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testQueryWithDifferentParameters() {
//        Map<String, String> params = new HashMap<>();
//        params.put("instance", dto.getInstance());
//
//        // 测试不同的指标名称
//        String[] testMetrics = {
//                "node_resources_disk_written_bytes_total",
//                "node_cpu_usage",
//                "node_memory_usage"
//        };
//
//        for (String metric : testMetrics) {
//            try {
//                dto.setIndexName(metric);
//                String query = ruleLibrary.getQuery(metric, params);
//                System.out.println("Metric: " + metric);
//                System.out.println("Generated Query: " + query);
//
//                if (query != null) {
//                    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
//                    System.out.println("Encoded Query: " + encodedQuery);
//                }
//                System.out.println("-------------------");
//
//            } catch (Exception e) {
//                System.err.println("Error processing metric: " + metric);
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Test
//    void testRuleLibraryImplementation() {
//        Map<String, String> params = new HashMap<>();
//        params.put("instance", "1");
//
//        String indexName = "node_resources_disk_written_bytes_total";
//        String query = ruleLibrary.getQuery(indexName, params);
//
//        // 打印查询规则的详细信息
//        System.out.println("Index Name: " + indexName);
//        System.out.println("Parameters: " + params);
//        System.out.println("Generated Query: " + query);
//
//        // 验证查询规则
//        assertNotNull(query, "Query should not be null for valid index name");
//        assertTrue(query.contains(indexName), "Query should contain the metric name");
//        assertTrue(query.contains(params.get("instance")), "Query should contain the instance parameter");
//    }
//}
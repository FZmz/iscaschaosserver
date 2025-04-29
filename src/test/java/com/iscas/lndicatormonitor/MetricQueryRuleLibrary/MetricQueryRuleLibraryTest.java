//package com.iscas.lndicatormonitor.MetricQueryRuleLibrary;
//
//import com.iscas.lndicatormonitor.rule.MetricQueryRuleLibrary;
//import com.iscas.lndicatormonitor.strategy.metricquery.ContainerNetworkErrorRateStrategy;
//import com.iscas.lndicatormonitor.strategy.metricquery.ContainerNetworkPacketLossRateStrategy;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@SpringBootTest
//public class MetricQueryRuleLibraryTest {
//
//    @Autowired
//    private MetricQueryRuleLibrary ruleLibrary;
//
//    /**
//     * 测试不同指标的查询语句生成
//     */
//    @Test
//    public void testMetricQueryGeneration() {
//        // 测试用例 1: 直接处理的指标
//        testMetric("container_fs_read_seconds_total", createParams("coroot", "coroot-54cddbdddd-tvvnf"));
//
//        // 测试用例 2: 特殊处理的指标
//        testMetric("container_network_packet_loss_rate",  createParams("coroot", "coroot-54cddbdddd-tvvnf"));
//
//        // 测试用例 3: 特殊处理的丢包率指标
//        testMetric("container_network_packet_loss_rate", createParams("coroot", "coroot-54cddbdddd-tvvnf"));
//
//        // 测试用例 4: 错误率指标
//        testMetric("container_network_error_rate", createParams("coroot", "coroot-54cddbdddd-tvvnf"));
//    }
//
//    /**
//     * 执行测试并打印结果
//     *
//     * @param metricName 指标名称
//     * @param params     标签参数
//     */
//    private void testMetric(String metricName, Map<String, String> params) {
//        String query = ruleLibrary.getQuery(metricName, params);
//        System.out.println("测试指标: " + metricName);
//        if (query != null) {
//            System.out.println("生成的查询语句: " + query);
//        } else {
//            System.out.println("未找到对应的处理策略，无法生成查询语句。");
//        }
//        System.out.println("-------------------------------------------------");
//    }
//
//    /**
//     * 创建标签参数
//     *
//     * @param namespace 命名空间
//     * @param podName   Pod 名称
//     * @return 标签参数 Map
//     */
//    private Map<String, String> createParams(String namespace, String podName) {
//        Map<String, String> params = new HashMap<>();
//        params.put("namespace", namespace);
//        params.put("pod", podName);
//        return params;
//    }
//}
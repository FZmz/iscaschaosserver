//package com.iscas.lndicatormonitor.CorootProcessorTest;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.iscas.lndicatormonitor.processor.IndexRecommend.coroot.CorootApplicationProcessor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class CorootApplicationProcessorTest {
//
//    @Autowired
//    private CorootApplicationProcessor corootApplicationProcessor;
//
//    @Test
//    public void testGetChartData() {
//        // 输入参数
//        String serviceName = "ts-order-service";
//        String namespace = "default";
//        String ownerReference = "Deployment";
//        long from = 1733190992022L;
//        long to = 1733191410000L;
//        String operationPath = "/data/reports/name:Dns/errors";
//
//        // 调用 getChartData 方法
//        JsonNode chartData = corootApplicationProcessor.getChartData(
//                serviceName, namespace, ownerReference, from, to, operationPath);
//
//        // 输出结果
//        if (chartData != null) {
//            System.out.println("找到的 chart 数据如下：");
//            System.out.println(chartData.toPrettyString());
//        } else {
//            System.out.println("未找到符合条件的 chart 数据。");
//        }
//
//        // 可选：添加断言，验证结果是否符合预期
//        // Assertions.assertNotNull(chartData, "Chart data should not be null");
//    }
//}
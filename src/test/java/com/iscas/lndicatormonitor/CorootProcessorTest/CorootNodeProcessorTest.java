//package com.iscas.lndicatormonitor.CorootProcessorTest;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.iscas.lndicatormonitor.processor.IndexRecommend.coroot.CorootNodeProcessor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class CorootNodeProcessorTest {
//    @Autowired
//    private CorootNodeProcessor corootNodeProcessor;
//    @Test
//    public void testGetWidgetData() {
//        // 测试参数
//        String nodeName = "1";
//        long from = 1733190992022L;
//        long to = 1733191410000L;
//        String operationPath = "/data/Node/chart/'CPU usage'";
//
//        // 调用 getWidgetData 方法
//        JsonNode widgetData = corootNodeProcessor.getWidgetData(nodeName, from, to, operationPath);
//
//        // 输出结果
//        if (widgetData != null) {
//            System.out.println("找到的 widget 数据如下：");
//            System.out.println(widgetData.toPrettyString());
//        } else {
//            System.out.println("未找到符合条件的 widget 数据。");
//        }
//
//        // 可选：使用断言来验证结果
//        // Assertions.assertNotNull(widgetData, "Widget data should not be null");
//    }
//}

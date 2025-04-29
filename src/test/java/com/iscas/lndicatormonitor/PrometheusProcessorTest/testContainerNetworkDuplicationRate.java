//package com.iscas.lndicatormonitor.PrometheusProcessorTest;
//
//import com.iscas.lndicatormonitor.dto.indexRecommend.PrometheusProcessorDTO;
//import com.iscas.lndicatormonitor.processor.IndexRecommend.prometheus.PrometheusNetworkProcessor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class testContainerNetworkDuplicationRate {
//    @Autowired
//    private PrometheusNetworkProcessor prometheusNetworkProcessor;
//
//    @Test
//    public void testContainerNetworkDuplicationRate() {
//        // 输入参数
//        PrometheusProcessorDTO dto = new PrometheusProcessorDTO();
//        dto.setNamespace("coroot");
//        dto.setPodName("coroot-54cddbdddd-tvvnf");
//        dto.setInstance(null);
//        long startMillis = 1733216751573L;
//        long endMillis = 1733217210000L;
//        dto.setFrom( startMillis);
//        dto.setTo( endMillis);
//        dto.setIndexName("container_network_duplication_rate");
//
//        // 调用方法并获取结果
//        Object result = prometheusNetworkProcessor.process(dto);
//
//        // 输出结果
//        System.out.println("Test Result for Container Network Duplication Rate:");
//        System.out.println(result);
//    }
//}

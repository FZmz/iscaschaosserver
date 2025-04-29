//package com.iscas.lndicatormonitor.PrometheusProcessorIntegrationTest;
//
//
//import com.iscas.lndicatormonitor.utils.NetworkInterfaceResolver;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
///**
// * 测试 PrometheusNetworkProcessor 类
// */
//@SpringBootTest
//public class PrometheusNetworkProcessorIntegrationTest {
//
//    @Autowired
//    private NetworkInterfaceResolver networkInterfaceResolver;
//
//    @Test
//    public void testGetPodNetworkInterface() {
//        // 输入参数
//        String namespace = "coroot";
//        String podName = "coroot-54cddbdddd-tvvnf";
//
//        // 调用方法
//        String networkInterface = networkInterfaceResolver.getPodNetworkInterface(namespace, podName);
//
//        // 输出结果
//        if (networkInterface != null) {
//            System.out.println("找到的网络接口名称为：" + networkInterface);
//        } else {
//            System.out.println("未能获取到网络接口名称。");
//        }
//    }
//}
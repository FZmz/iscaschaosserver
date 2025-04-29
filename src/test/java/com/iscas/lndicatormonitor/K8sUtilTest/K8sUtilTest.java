//package com.iscas.lndicatormonitor.K8sUtilTest;
//
//import com.iscas.lndicatormonitor.utils.K8sUtil;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.springframework.test.util.AssertionErrors.assertNotNull;
//
//@SpringBootTest
//public class K8sUtilTest {
//    @Autowired
//    private K8sUtil k8sUtil;
//
//    @Test
//    void testGetServiceNameByNamespaceAndPodName() {
//        String namespace = "default";
//        String podName = "ts-travel-service-75455f5577-j4prn";
//
//        String serviceName = k8sUtil.getServiceNameByNamespaceAndPodName(namespace, podName);
//
//        assertNotNull(serviceName, "Service name should not be null");
//        System.out.println("Service name for pod " + podName + ": " + serviceName);
//    }
//
//    @Test
//    void testGetManagerByNamespaceAndPodName() {
//        String namespace = "default";
//        String podName = "ts-travel-service-75455f5577-j4prn";
//
//        String managerName = k8sUtil.getManagerTypeByNamespaceAndPodName(namespace, podName);
//
//        assertNotNull(managerName, "Manager name should not be null");
//        System.out.println("Manager name for pod " + podName + ": " + managerName);
//    }
//}

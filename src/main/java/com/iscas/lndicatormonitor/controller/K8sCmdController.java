package com.iscas.lndicatormonitor.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.dto.faultConfig.SelectorRequest;
import com.iscas.lndicatormonitor.utils.K8sUtil;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference; // TypeReference

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/k8sCmd")
@Slf4j
public class K8sCmdController {
    @Autowired
    private CorootController corootController;

    @Autowired
    private KubernetesClient kubernetesClient;

    @Autowired
    private K8sUtil k8sUtil;
    @GetMapping("/describeResource")
    public String describeResource(
            @RequestParam String namespace,
            @RequestParam String resourceType,
            @RequestParam String resourceName,
            @RequestParam("startTime") long startTime,
            @RequestParam("endTime") long endTime) {

        StringBuilder result = new StringBuilder();

        try {
            // 根据资源类型获取资源描述
            switch (resourceType.toLowerCase()) {
                case "pod":
                    Pod pod = kubernetesClient.pods().inNamespace(namespace).withName(resourceName).get();
                    result.append(pod != null ? pod.toString() : "Pod not found");
                    break;

                case "service":
                    Service service = kubernetesClient.services().inNamespace(namespace).withName(resourceName).get();
                    result.append(service != null ? service.toString() : "Service not found");
                    break;

                case "deployment":
                    Deployment deployment = kubernetesClient.apps().deployments().inNamespace(namespace).withName(resourceName).get();
                    result.append(deployment != null ? deployment.toString() : "Deployment not found");
                    break;

                default:
                    return "Unsupported resource type: " + resourceType;
            }
        } catch (Exception e) {
            // 如果资源获取失败，尝试获取依赖映射
            result.append("Failed to get resource description: ").append(e.getMessage()).append("\n");
            appendDependencyInfo(result, namespace, resourceName, startTime, endTime);
        }

        return result.toString();
    }
    private void appendDependencyInfo(StringBuilder result, String namespace, String resourceName, long startTime, long endTime) {
        try {
            CustomResult dependencyMap = corootController.getDependencyMap(resourceName, namespace, startTime, endTime);
            ObjectNode data = (ObjectNode) dependencyMap.getData();
            JsonNode nodes = data.get("nodes");

            if (nodes != null) {
                for (JsonNode node : nodes) {
                    JsonNode srcInstances = node.get("src_instances");
                    if (srcInstances != null) {
                        for (JsonNode srcInstance : srcInstances) {
                            String podName = srcInstance.get("name").asText();
                            Pod pod = kubernetesClient.pods().inNamespace(namespace).withName(podName).get();
                            result.append(pod != null ? pod.toString() : "Pod not found for src_instance: " + podName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            result.append("Failed to fetch dependency information: ").append(e.getMessage());
        }
    }

   @GetMapping("/getServiceLabel")
    public CustomResult getServiceLabel(@RequestParam String namespace, @RequestParam String serviceName) {
       Map<String,String> selector = k8sUtil.getServiceSelector(serviceName, namespace);
       return CustomResult.ok(selector);
    }


    /**
     * 根据 Selector 获取匹配的 Service 名称列表
     *
     * @param request 包含 namespace 和 selector 的请求对象
     * @return 匹配的 Service 名称列表
     */
    @PostMapping("/getServiceNamesBySelector")
    public CustomResult getServiceNamesBySelector(@RequestBody SelectorRequest request) {
        try {
            // 从请求对象中获取参数
            String namespace = request.getNamespace();
            Map<String, String> selector = request.getSelector();

            // 调用 k8sUtil 获取服务名称列表
            List<String> serviceNames = k8sUtil.getServiceNamesBySelector(namespace, selector);
            return CustomResult.ok(serviceNames);
        } catch (Exception e) {
            return CustomResult.fail("Failed to fetch service names: " + e.getMessage());
        }
    }

    @GetMapping("/getNodeNameByNodeIp")
    public CustomResult getNodeNameByNodeIp(@RequestParam String nodeIp) {
        try {
            // 获取所有节点
            return kubernetesClient.nodes()
                    .list()
                    .getItems()
                    .stream()
                    .filter(node -> node.getStatus().getAddresses().stream()
                            .anyMatch(address -> 
                                // 匹配内部IP或外部IP
                                (address.getType().equals("InternalIP") || 
                                 address.getType().equals("ExternalIP")) && 
                                address.getAddress().equals(nodeIp)
                            ))
                    .findFirst()
                    .map(node -> CustomResult.ok(node.getMetadata().getName()))
                    .orElse(CustomResult.fail("未找到对应IP的节点: " + nodeIp));

        } catch (Exception e) {
            log.error("根据Node IP获取节点名称失败: {}", e.getMessage(), e);
            return CustomResult.fail("获取节点名称失败: " + e.getMessage());
        }
    }
}

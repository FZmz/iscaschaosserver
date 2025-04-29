package com.iscas.lndicatormonitor.utils;

import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.ServiceResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class K8sUtil {
    @Autowired
    private KubernetesClient kubernetesClient;

    /**
     * 根据 namespace 和 podName 获取对应的 serviceName
     *
     * @param namespace 命名空间
     * @param podName   Pod 名称
     * @return serviceName 或 null 如果未找到
     */
    public String getServiceNameByNamespaceAndPodName(String namespace, String podName) {
        // 获取所有服务
        NonNamespaceOperation<Service, ServiceList, ServiceResource<Service>> services = kubernetesClient.services().inNamespace(namespace);
        // 遍历服务，找到与 Pod 关联的服务
        for (Service service : services.list().getItems()) {
            if (service.getSpec().getSelector() != null && !service.getSpec().getSelector().isEmpty()) {
                // 匹配 Pod 标签
                boolean matches = kubernetesClient.pods()
                        .inNamespace(namespace)
                        .withName(podName)
                        .get()
                        .getMetadata()
                        .getLabels()
                        .entrySet()
                        .containsAll(service.getSpec().getSelector().entrySet());
                if (matches) {
                    return service.getMetadata().getName();
                }
            }
        }
        return null; // 未找到服务
    }

    /**
     * 根据 namespace 和 podName 获取对应的顶层 manager 的类型 (Deployment, DaemonSet, StatefulSet, ReplicaSet, Job, CronJob)
     *
     * @param namespace 命名空间
     * @param podName   Pod 名称
     * @return manager 的类型或 null 如果未找到
     */
    public String getManagerTypeByNamespaceAndPodName(String namespace, String podName) {
        // 获取 Pod
        Pod pod = kubernetesClient.pods().inNamespace(namespace).withName(podName).get();
        if (pod == null || pod.getMetadata().getOwnerReferences() == null
                || pod.getMetadata().getOwnerReferences().isEmpty()) {
            return null;
        }

        // 递归解析 OwnerReference，找到最顶层的 Manager 类型
        return resolveOwnerReferenceForType(namespace, pod.getMetadata().getOwnerReferences().get(0));
    }

    /**
     * 递归解析 OwnerReference，返回顶层 Manager 的类型
     *
     * @param namespace       命名空间
     * @param ownerReference  当前资源的 OwnerReference
     * @return 顶层 Manager 的类型或 null
     */
    private String resolveOwnerReferenceForType(String namespace, OwnerReference ownerReference) {
        String ownerName = ownerReference.getName();
        String ownerKind = ownerReference.getKind();

        switch (ownerKind) {
            case "Deployment":
            case "StatefulSet":
            case "DaemonSet":
            case "Job":
            case "CronJob":
                // 如果是顶层控制器，直接返回其类型
                return ownerKind;

            case "ReplicaSet":
                ReplicaSet replicaSet = kubernetesClient.apps().replicaSets()
                        .inNamespace(namespace).withName(ownerName).get();
                if (replicaSet != null && replicaSet.getMetadata().getOwnerReferences() != null
                        && !replicaSet.getMetadata().getOwnerReferences().isEmpty()) {
                    // 继续递归解析 ReplicaSet 的 OwnerReferences
                    return resolveOwnerReferenceForType(namespace, replicaSet.getMetadata().getOwnerReferences().get(0));
                } else {
                    // 如果没有 OwnerReferences，返回 ReplicaSet 的类型
                    return ownerKind;
                }

            default:
                // 其他类型，视情况处理，返回其类型
                return ownerKind;
        }
    }

    /**
     * 根据服务名称和命名空间获取服务的 selector
     *
     * @param serviceName 服务名称
     * @param namespace   命名空间
     * @return 服务的 selector（键值对形式），如果服务不存在则返回 null
     */
    public Map<String, String> getServiceSelector(String serviceName, String namespace) {
        try {
            // 从 Kubernetes 获取服务
            Service service = kubernetesClient.services().inNamespace(namespace).withName(serviceName).get();
            if (service == null) {
                throw new RuntimeException(String.format("Service %s not found in namespace %s", serviceName, namespace));
            }

            // 获取服务的 selector
            Map<String, String> selector = service.getSpec().getSelector();
            if (selector == null || selector.isEmpty()) {
                throw new RuntimeException(String.format("Selector not defined for service %s in namespace %s", serviceName, namespace));
            }

            return selector;
        } catch (Exception e) {
            // 打印错误日志并抛出异常
            throw new RuntimeException("Failed to get selector for service " + serviceName + " in namespace " + namespace, e);
        }
    }



    /**
     * 根据 Service 的 Selector 获取匹配的 ServiceName 列表
     *
     * @param namespace 指定的命名空间
     * @param selector  Service 的 Selector (key-value 键值对)
     * @return 匹配的 ServiceName 列表
     */
    public List<String> getServiceNamesBySelector(String namespace, Map<String, String> selector) {
        // 获取命名空间下的所有服务
        ServiceList serviceList = kubernetesClient.services().inNamespace(namespace).list();

        // 筛选出 selector 匹配的服务
        return serviceList.getItems().stream()
                .filter(service -> {
                    Map<String, String> serviceSelector = Optional.ofNullable(service.getSpec().getSelector())
                            .orElse(Collections.emptyMap()); // 替代 Map.of()
                    return selector.entrySet().stream()
                            .allMatch(entry -> serviceSelector.getOrDefault(entry.getKey(), "").equals(entry.getValue()));
                })
                .map(service -> service.getMetadata().getName()) // 提取服务名称
                .collect(Collectors.toList());
    }

}

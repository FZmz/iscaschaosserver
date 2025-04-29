package com.iscas.lndicatormonitor.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Connection;
import com.iscas.lndicatormonitor.domain.Service;
import com.iscas.lndicatormonitor.service.ConnectionService;
import com.iscas.lndicatormonitor.service.ServiceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/service")
public class ServiceController {

    private static final Logger logger = Logger.getLogger(CorootController.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private ServiceService serviceService;
    @Value("${coroot.url}")
    private String corootUrl;

    @Value("${coroot.projectId}")
    private String projectId;
    @GetMapping(value = "/getServiceList")
    @OperationLog("获取服务列表")
    public CustomResult getServiceList(@RequestParam("namespace") String namespace, @RequestParam("applicationId") String applicationId) {
        // 定义请求 URL
        String requestUrl = corootUrl +  projectId + "/overview/health?query=&from=now-3h";
        logger.info("Request URL: " + requestUrl);

        // 发送 GET 请求并获取响应
        ResponseEntity<JsonNode> response;
        try {
            logger.info("Sending GET request to fetch health data");
            response = restTemplate.getForEntity(requestUrl, JsonNode.class);
            logger.info("Response received with status code: " + response.getStatusCode());
        } catch (Exception e) {
            logger.error("Failed to send GET request", e);
            return CustomResult.fail("Failed to fetch health data");
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Failed to fetch health data, status code: " + response.getStatusCode());
            return CustomResult.fail("Failed to fetch health data");
        }

        List<Service> filterServiceList = new ArrayList<>();
        List<JsonNode> serviceHealth = new ArrayList<>();
        try {
            logger.info("Parsing response body");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(String.valueOf(response.getBody()));
            JsonNode healthNodes = root.at("/data/health");

            if (healthNodes.isArray()) {
                for (JsonNode healthNode : healthNodes) {
                    String id = healthNode.get("id").asText();
                    if (id.startsWith(namespace + ":Deployment:")) {
                        String serviceName = id.substring(id.lastIndexOf(":") + 1);
                        Service service = new Service();
                        service.setApplicationId(applicationId);
                        service.setName(serviceName);
                        filterServiceList.add(service);
                        ((ObjectNode) healthNode).put("id", serviceName);
                        serviceHealth.add(healthNode);
                    }
                }
                logger.info("Filtered service list count: " + filterServiceList.size());
            } else {
                logger.warn("Health nodes is not an array");
            }
        } catch (Exception e) {
            logger.error("Failed to parse health data", e);
            return CustomResult.fail("Failed to parse health data");
        }

        // 获取数据库中的服务信息
        logger.info("Fetching services from the database for applicationId: " + applicationId);
        List<Service> lastServiceList;
        try {
            lastServiceList = serviceService.getByApplicationId(applicationId);
            lastServiceList.forEach(service -> service.setId(null)); // 去掉 id
            logger.info("Services fetched from database count: " + lastServiceList.size());
        } catch (Exception e) {
            logger.error("Failed to fetch services from the database", e);
            return CustomResult.fail("Failed to fetch services from the database");
        }
        filterServiceList.forEach(service -> service.setId(null) );
        // 取 filterServiceList 和 lastServiceList 的并集
        Set<Service> unionSet = new HashSet<>(filterServiceList);
        unionSet.addAll(lastServiceList);
        logger.info("Union set size: " + unionSet.size());

        // 取 unionSet 与 filterServiceList 的余集（即 unionSet 中多出来的部分）
        Set<Service> diffSet = new HashSet<>(unionSet);
        diffSet.removeAll(lastServiceList);
        logger.info("Difference set size (new services to be added): " + diffSet.size());

        // 将余集存入数据库
        try {
            if (!diffSet.isEmpty()) {
                serviceService.saveBatch(new ArrayList<>(diffSet));
                logger.info("Difference set saved to database successfully");
            } else {
                logger.info("No new services to save to the database");
            }
        } catch (Exception e) {
            logger.error("Failed to save services to the database", e);
            return CustomResult.fail("Failed to save services to the database");
        }

        // 匹配 serviceHealth 和 unionSet
        List<JsonNode> result = new ArrayList<>();
        unionSet.forEach(service -> {
            JsonNode matchingHealth = serviceHealth.stream()
                    .filter(health -> health.get("id").asText().equals(service.getName()))
                    .findFirst()
                    .orElseGet(() -> {
                        ObjectMapper mapper = new ObjectMapper();
                        ObjectNode newNode = mapper.createObjectNode();
                        newNode.put("id", service.getName());
                        newNode.put("category", "application");
                        newNode.put("status", "offline");
                        return newNode;
                    });
            result.add(matchingHealth);
        });
        return CustomResult.ok(result);
    }
    @GetMapping(value = "/getServiceListNew")
    @OperationLog("获取服务列表")
    public CustomResult getServiceListNew(@RequestParam("namespace") String namespace) {
        // 定义请求 URL
        String requestUrl = corootUrl +  projectId + "/overview/health?query=&from=now-3h";
        logger.info("Request URL: " + requestUrl);

        // 发送 GET 请求并获取响应
        ResponseEntity<JsonNode> response;
        try {
            logger.info("Sending GET request to fetch health data");
            response = restTemplate.getForEntity(requestUrl, JsonNode.class);
            logger.info("Response received with status code: " + response.getStatusCode());
        } catch (Exception e) {
            logger.error("Failed to send GET request", e);
            return CustomResult.fail("Failed to fetch health data");
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Failed to fetch health data, status code: " + response.getStatusCode());
            return CustomResult.fail("Failed to fetch health data");
        }

        try {
            logger.info("Parsing response body");
            ObjectMapper mapper = new ObjectMapper();
            List<JsonNode> serviceList = new ArrayList<>();
            JsonNode root = mapper.readTree(String.valueOf(response.getBody()));
            JsonNode healthNodes = root.at("/data/health");
            if (healthNodes.isArray()) {
                for (JsonNode healthNode : healthNodes) {
                    String id = healthNode.get("id").asText();
                    if (id.startsWith(namespace)) {
                        serviceList.add(healthNode);
                    }
                }
            } else {
                logger.warn("Health nodes is not an array");
            }
            return CustomResult.ok(serviceList);
        } catch (Exception e) {
            logger.error("Failed to parse health data", e);
            return CustomResult.fail("Failed to parse health data");
        }
    }


    @GetMapping(value = "/getFilteredMapNodes")
    @OperationLog("获取连接列表")
    public CustomResult getFilteredMapNodes(@RequestParam("namespace") String namespace, @RequestParam("applicationId") String applicationId) {
        Logger logger = Logger.getLogger(getClass());

        // 定义请求 URL
        String requestUrl = corootUrl + projectId +  "/overview/map?query=";
        logger.info("Request URL: " + requestUrl);

        // 发送 GET 请求并获取响应
        ResponseEntity<JsonNode> response;
        try {
            logger.info("Sending GET request to fetch map data");
            response = restTemplate.getForEntity(requestUrl, JsonNode.class);
            logger.info("Response received with status code: " + response.getStatusCode());
        } catch (Exception e) {
            logger.error("Failed to send GET request", e);
            return CustomResult.fail("Failed to fetch map data");
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Failed to fetch map data, status code: " + response.getStatusCode());
            return CustomResult.fail("Failed to fetch map data");
        }

        List<Connection> filteredMapNodes = new ArrayList<>();
        try {
            logger.info("Parsing response body");
            ObjectMapper mapper = new ObjectMapper();
            String responseBody = String.valueOf(response.getBody());
            JsonNode root = mapper.readTree(responseBody);
            JsonNode mapNodes = root.at("/data/map");

            if (mapNodes.isArray()) {
                for (JsonNode node : mapNodes) {
                    if (namespace.equals(node.at("/labels/ns").asText())) {
                        JsonNode upstreams = node.get("upstreams");
                        JsonNode downstreams = node.get("downstreams");

                        if (upstreams != null && upstreams.isArray()) {
                            for (JsonNode upstream : upstreams) {
                                Connection connection = new Connection();
                                connection.setApplicationId(applicationId);
                                connection.setFromId(upstream.get("id").asText());
                                connection.setToId(node.get("id").asText());
                                filteredMapNodes.add(connection);
                            }
                        }

                        if (downstreams != null && downstreams.isArray()) {
                            for (JsonNode downstream : downstreams) {
                                Connection connection = new Connection();
                                connection.setApplicationId(applicationId);
                                connection.setFromId(node.get("id").asText());
                                connection.setToId(downstream.get("id").asText());
                                filteredMapNodes.add(connection);
                            }
                        }
                    }
                }
                logger.info("Filtered map nodes count: " + filteredMapNodes.size());
            } else {
                logger.warn("Map nodes is not an array");
            }
        } catch (Exception e) {
            logger.error("Failed to parse map data", e);
            return CustomResult.fail("Failed to parse map data");
        }

        // 去重 filteredMapNodes
        filteredMapNodes = new ArrayList<>(filteredMapNodes.stream().distinct().collect(Collectors.toList()));
        logger.info("Filtered map nodes count after deduplication: " + filteredMapNodes.size());

        // 获取数据库中的连接信息
        logger.info("Fetching connections from the database for applicationId: " + applicationId);
        List<Connection> lastMapNodes;
        try {
            lastMapNodes = connectionService.getByApplicationId(applicationId);
            lastMapNodes.forEach(connection -> {
                connection.setId(null); // 去掉 id
            });
            logger.info("Connections fetched from database count: " + lastMapNodes.size());
        } catch (Exception e) {
            logger.error("Failed to fetch connections from the database", e);
            return CustomResult.fail("Failed to fetch connections from the database");
        }

        filteredMapNodes.forEach(connection -> {
            connection.setId(null); // 去掉 id
        });
        // 取 filtered_map_nodes 和 last_map_nodes 的并集
        Set<Connection> unionSet = new HashSet<>(filteredMapNodes);
        unionSet.addAll(lastMapNodes);
        logger.info("Union set size: " + unionSet.size());

        // 取 unionSet 与 lastMapNodes 的余集（即 unionSet 中多出来的部分）
        Set<Connection> diffSet = new HashSet<>(unionSet);
        diffSet.removeAll(lastMapNodes);
        logger.info("Difference set size (new connections to be added): " + diffSet.size());

        // 将余集存入数据库
        try {
            if (!diffSet.isEmpty()) {
                connectionService.saveBatch(diffSet);
                logger.info("Difference set saved to database successfully");
            } else {
                logger.info("No new connections to save to the database");
            }
        } catch (Exception e) {
            logger.error("Failed to save connections to the database", e);
            return CustomResult.fail("Failed to save connections to the database");
        }

        // 去掉返回值中的 id
        List<Map<String, Object>> result = unionSet.stream().map(connection -> {
            Map<String, Object> connMap = new HashMap<>();
            connMap.put("applicationId", connection.getApplicationId());
            connMap.put("fromId", connection.getFromId());
            connMap.put("toId", connection.getToId());
            return connMap;
        }).collect(Collectors.toList());

        return CustomResult.ok(result);
    }


}

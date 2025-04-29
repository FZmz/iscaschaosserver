package com.iscas.lndicatormonitor.processor.IndexRecommend.coroot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class CorootApplicationProcessor {
    private static final Logger log = LoggerFactory.getLogger(CorootApplicationProcessor.class);

    @Value("${coroot.url}")
    private String corootUrl;

    @Value("${coroot.projectId}")
    private String projectId;

    @Value("${coroot.cookie}")
    private String cookie;

    @Value("${kubernetes.token}")
    private String token;

    @Autowired
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getChartData(String serviceName, String namespace, String ownerReference,
                                 long from, long to, String operationPath) {
        System.out.println("serviceName: " + serviceName);
        log.info("开始获取图表数据，参数：服务名称={}，命名空间={}，所有者引用={}，开始时间={}，结束时间={}，操作路径={}",
                serviceName, namespace, ownerReference, from, to, operationPath);

        try {
            // 从 ownerReference 获取 manager 名称
            String manager = getManagerFromOwnerReference(ownerReference);
            log.info("从所有者引用中提取的管理器：{}", manager);

            if (manager == null) {
                log.warn("从所有者引用中提取管理器失败：{}", ownerReference);
                return null;
            }

            // 构建请求 URL
            String url = String.format("%s%s/app/%s:%s:%s?from=%d&to=%d",
                    corootUrl, projectId, namespace, manager, serviceName, from, to);
            log.info("构建的请求URL：{}", url);

            // 创建请求头，添加 Cookie
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.COOKIE, cookie);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            log.info("已创建HTTP请求头，包含Cookie和Accept类型");

            // 创建请求实体
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 发起 GET 请求
            log.info("正在向Coroot API发送GET请求...");
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // 检查响应状态码
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                log.error("请求失败，状态码：{}", responseEntity.getStatusCode());
                return null;
            }
            log.debug("成功接收到Coroot API的响应");

            // 解析 JSON 响应
            String responseBody = responseEntity.getBody();
            log.debug("响应体长度：{}", responseBody != null ? responseBody.length() : 0);

            JsonNode rootNode = objectMapper.readTree(responseBody);
            log.debug("成功解析JSON响应");

            // 根据 operationPath 定位数据
            JsonNode result = findChartData(rootNode, operationPath);
            if (result == null) {
                log.warn("未找到操作路径对应的图表数据：{}", operationPath);
            } else {
                log.info("成功找到操作路径对应的图表数据：{}", operationPath);
            }

            return result;

        } catch (Exception e) {
            log.error("获取图表数据时发生错误：", e);
            log.error("错误详情 - 消息：{}，原因：{}", e.getMessage(), e.getCause());
            return null;
        } finally {
            log.info("图表数据获取方法执行完毕");
        }
    }

    private String getManagerFromOwnerReference(String ownerReference) {
        log.debug("开始从所有者引用中提取管理器，所有者引用：{}", ownerReference);

        String result = ownerReference != null && !ownerReference.isEmpty() ? ownerReference : null;

        if (result == null) {
            log.warn("提供的所有者引用无效：{}", ownerReference);
        } else {
            log.debug("成功提取管理器：{}", result);
        }

        return result;
    }

    private JsonNode findChartData(JsonNode rootNode, String operationPath) {
        log.info("开始查找图表数据，操作路径：{}", operationPath);

        // 解析 operationPath，提取条件
        String[] pathSegments = operationPath.split("/");
        log.info("操作路径段：{}", String.join(", ", pathSegments));

        String targetReportName = null;
        String targetChartTitleKeyword = null;

        // 解析路径获取目标名称和关键字
        for (String segment : pathSegments) {
            if (segment.startsWith("name:")) {
                targetReportName = segment.substring(5);
                log.info("目标报告名称：{}", targetReportName);
            } else if (!segment.isEmpty()) {
                targetChartTitleKeyword = segment;
                log.info("目标图表关键字：{}", targetChartTitleKeyword);
            }
        }

        // 定位到 reports 节点
        JsonNode reportsNode = rootNode.path("data").path("reports");
        if (reportsNode.isMissingNode()) {
            log.warn("未找到reports节点");
            return null;
        }

        for (JsonNode reportNode : reportsNode) {
            String reportName = reportNode.path("name").asText();
            log.info("检查报告 - 名称：{}, 目标名称：{}", reportName, targetReportName);

            if (reportName.equalsIgnoreCase(targetReportName)) {
                log.info("找到目标报告：{}", reportName);
                JsonNode widgetsNode = reportNode.path("widgets");
                log.info("找到目标报告长度：{}", widgetsNode.size());

                // 清理目标关键字，去除可能存在的引号
                String cleanTargetKeyword = targetChartTitleKeyword;
                if (cleanTargetKeyword.startsWith("'") && cleanTargetKeyword.endsWith("'")) {
                    cleanTargetKeyword = cleanTargetKeyword.substring(1, cleanTargetKeyword.length() - 1);
                }

                // 确定是否为特殊报告类型（CPU或Memory）
                boolean isSpecialReport = "CPU".equalsIgnoreCase(targetReportName) ||
                        "Memory".equalsIgnoreCase(targetReportName);

                // 打印所有图表标题以便调试
                for (JsonNode widget : widgetsNode) {
                    JsonNode chartNode = isSpecialReport ?
                            widget.path("chart_group") : widget.path("chart");

                    if (!chartNode.isMissingNode()) {
                        log.info("发现图表，标题：'{}'", chartNode.path("title").asText());
                    }
                }

                // 查找匹配的图表
                for (JsonNode widget : widgetsNode) {
                    // 根据报告类型选择正确的节点路径
                    JsonNode chartNode = isSpecialReport ?
                            widget.path("chart_group") : widget.path("chart");

                    if (!chartNode.isMissingNode()) {
                        String chartTitle = chartNode.path("title").asText();
                        log.info("比较图表 - 当前标题：'{}', 目标关键字：'{}', 清理后关键字：'{}'",
                                chartTitle, targetChartTitleKeyword, cleanTargetKeyword);

                        // 标题精确匹配或关键字匹配，使用清理后的关键字
                        boolean isMatch = chartTitle.equals(cleanTargetKeyword) ||
                                chartTitle.contains(cleanTargetKeyword) ||
                                cleanTargetKeyword.contains(chartTitle);

                        if (isMatch) {
                            System.out.println("chartTitle: " + chartTitle);
                            log.info("找到匹配图表：{}", chartTitle);
                            // 打印完整的图表数据用于调试
                            try {
                                log.debug("图表数据：{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartNode));
                            } catch (Exception e) {
                                log.warn("图表数据打印失败", e);
                            }
                            return chartNode;
                        }
                    }
                }
                log.warn("在报告中未找到匹配的图表");
                return null;
            }
        }
        log.warn("未找到匹配的报告和图表");
        return null;
    }
}
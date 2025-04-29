package com.iscas.lndicatormonitor.processor.IndexRecommend.coroot;

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
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CorootNodeProcessor {

    private static final Logger log = LoggerFactory.getLogger(CorootNodeProcessor.class);

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

    /**
     * 根据输入参数获取指定的 Widget 数据
     *
     * @param nodeName      节点名称
     * @param from          开始时间戳
     * @param to            结束时间戳
     * @param operationPath 操作路径，例如 "/data/Node/chart_group/'Disk space'"
     * @return 符合条件的 Widget 数据的 JsonNode，或 null 如果未找到
     */
    public JsonNode getWidgetData(String nodeName, long from, long to, String operationPath) {
        log.info("开始获取Widget数据 - 节点名称: {}, 开始时间: {}, 结束时间: {}, 操作路径: {}",
                nodeName, from, to, operationPath);
        try {
            // 构建请求 URL
            String url = String.format("%s%s/node/%s?from=%d&to=%d",
                    corootUrl, projectId, nodeName, from, to);
            log.debug("构建的请求URL: {}", url);

            // 创建请求头，添加 Cookie
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.COOKIE, cookie);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            log.debug("设置请求头完成 - Cookie和Accept已添加");

            // 创建请求实体
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 发起 GET 请求
            log.info("正在发送HTTP GET请求到Coroot服务器...");
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // 检查响应状态码
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                log.error("请求失败 - HTTP状态码: {}, URL: {}", responseEntity.getStatusCode(), url);
                return null;
            }
            log.info("成功接收到响应 - HTTP状态码: {}", responseEntity.getStatusCode());

            // 获取响应体
            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            log.debug("已成功解析响应JSON数据");

            // 根据 operationPath 定位数据
            JsonNode result = findWidgetData(rootNode, operationPath);
            if (result != null) {
                log.info("成功找到Widget数据");
            } else {
                log.warn("未找到匹配的Widget数据");
            }
            return result;

        } catch (Exception e) {
            log.error("获取Widget数据时发生异常 - 节点名称: {}, 错误信息: {}", nodeName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据 operationPath 在 JSON 数据中查找并返回目标 Widget 数据
     *
     * @param rootNode      JSON 根节点
     * @param operationPath 操作路径，例如 "/data/Node/chart_group/'Disk space'"
     * @return 符合条件的 Widget 数据的 JsonNode，或 null 如果未找到
     */
    private JsonNode findWidgetData(JsonNode rootNode, String operationPath) {
        log.info("开始查找Widget数据 - 操作路径: {}", operationPath);

        // 检查 resp.data.name == "Node"
        JsonNode dataNode = rootNode.path("data");
        String dataName = dataNode.path("name").asText();
        log.debug("检查数据节点名称 - 期望: Node, 实际: {}", dataName);

        if (!"Node".equalsIgnoreCase(dataName)) {
            log.error("数据节点名称不匹配 - 期望: Node, 实际: {}", dataName);
            return null;
        }

        // 解析 operationPath，提取目标键和标题
        String[] keyAndTitle = parseOperationPath(operationPath);
        if (keyAndTitle == null) {
            log.error("操作路径解析失败 - 路径: {}", operationPath);
            return null;
        }
        String targetKey = keyAndTitle[0];    // 例如 "chart_group"
        String targetTitle = keyAndTitle[1];  // 例如 "Disk space"
        log.info("已解析操作路径 - 目标键: {}, 目标标题: {}", targetKey, targetTitle);

        // 遍历 widgets，查找包含目标键和标题的节点
        JsonNode widgetsNode = dataNode.path("widgets");
        log.info("开始遍历widgets节点，查找匹配项");

        for (JsonNode widgetNode : widgetsNode) {
            // 获取指定的键
            JsonNode targetNode = widgetNode.path(targetKey);
            if (!targetNode.isMissingNode()) {
                String title = targetNode.path("title").asText(null);
                log.info("检查widget - 键: {}, 标题: {}", targetKey, title);

                if (title != null && title.contains(targetTitle)) {
                    log.info("找到匹配的widget - 键: {}, 标题: {}", targetKey, title);
                    return widgetNode;
                }
            }
        }

        log.warn("未找到匹配的widget - 目标键: {}, 目标标题: {}", targetKey, targetTitle);
        return null;
    }

    /**
     * 解析 operationPath，提取目标键和标题
     *
     * @param operationPath 操作路径
     * @return 一个包含键和标题的字符串数组，数组长度为2，索引0为键，索引1为标题；如果解析失败，返回 null
     */
    private String[] parseOperationPath(String operationPath) {
        log.debug("开始解析操作路径: {}", operationPath);

        // 使用正则表达式处理可能包含斜杠的路径
        // 例如: "/data/Node/chart_group/'I/O load'"
        Pattern pattern = Pattern.compile("/data/Node/([^/]+)/'([^']+)'");
        Matcher matcher = pattern.matcher(operationPath);

        if (matcher.find()) {
            String key = matcher.group(1);
            String title = matcher.group(2);

            log.debug("操作路径解析完成 - 键: {}, 标题: {}", key, title);
            return new String[]{key, title};
        }

        // 如果没有单引号，尝试常规的分割方法
        String[] pathSegments = operationPath.split("/");
        if (pathSegments.length < 4) {
            log.error("操作路径格式错误 - 路径段数量不足: {}", pathSegments.length);
            return null;
        }

        String key = pathSegments[3];

        // 处理第四段，可能需要重新组合被分割的部分
        StringBuilder titleBuilder = new StringBuilder();
        for (int i = 4; i < pathSegments.length; i++) {
            if (i > 4) {
                titleBuilder.append("/");
            }
            titleBuilder.append(pathSegments[i]);
        }

        String title = titleBuilder.toString().replace("'", "").replace("\"", "");

        log.debug("操作路径解析完成 - 键: {}, 标题: {}", key, title);
        return new String[]{key, title};
    }
}
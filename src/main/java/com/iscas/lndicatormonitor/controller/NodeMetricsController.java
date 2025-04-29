package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/nodeMetrics")
@Slf4j
public class NodeMetricsController {
    
    @Value("${coroot.url}")
    private String corootUrl;

    @Value("${coroot.projectId}")
    private String projectId;

    @Value("${coroot.cookie}")
    private String cookie;

    @GetMapping("/node")
    @OperationLog("获取节点指标")
    public CustomResult getNodeMetrics(@RequestParam String nodeName,
                                       @RequestParam Long from,
                                       @RequestParam Long to) {
        try {
            String url = String.format("%s/%s/node/%s?from=%d&to=%d",
                    corootUrl,
                    projectId,
                    nodeName,
                    from,
                    to);

            log.info("请求URL: {}", url);

            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);
            headers.set("Accept", "*/*");

            // 发送请求
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Object> response = new RestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                return CustomResult.fail("获取节点指标失败: " + response.getStatusCode());
            }

            return CustomResult.ok(response.getBody());

        } catch (Exception e) {
            log.error("获取节点指标失败, nodeName: {}, from: {}, to: {}, error: {}",
                    nodeName, from, to, null, e);
            return CustomResult.fail("获取节点指标失败: " + null);
        }
    }
} 
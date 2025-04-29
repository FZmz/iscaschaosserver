package com.iscas.lndicatormonitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Component
@Slf4j
public class LoadTestUtils {
    @Value("${loadtestservicelist.runTest.url}")
    private String runTestUrl;

    @Value("${loadtestservicelist.stopTest.url}")
    private String stopTestUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public String startLoadTest(String loadId) throws Exception {
        log.info("开始启动压测，压测ID：{}", loadId);
        String url = runTestUrl.replace("{loadId}", loadId);
        log.debug("压测启动URL：{}", url);

        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
            log.info("发送压测启动请求");
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            log.info("压测启动请求发送成功，响应内容：{}", response.getBody());
            
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String taskId = jsonNode.get("data").asText();
            log.info("获取到压测任务ID：{}", taskId);
            return taskId;
        } catch (Exception e) {
            log.error("压测启动失败，压测ID：{}，异常信息：{}", loadId, e.getMessage(), e);
            throw e;
        }
    }

    public void stopLoadTest(String loadId) {
        log.info("开始停止压测，压测ID：{}", loadId);
        String url = stopTestUrl.replace("{loadId}", loadId);
        log.debug("压测停止URL：{}", url);

        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
            log.info("发送压测停止请求");
            restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            log.info("压测停止请求发送成功");
        } catch (Exception e) {
            log.error("压测停止失败，压测ID：{}，异常信息：{}", loadId, e.getMessage(), e);
            throw e;
        }
    }
}
package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.dto.chaos.QuestPodBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/chaos")
public class ChaosMeshController {
    @Value("${chaos.mesh.url}")
    private String chaosMeshUrl;
    @Value("${chaos.mesh.token}")
    private String chaosMeshToken;
    @Autowired
    private RestTemplate restTemplate;
    // 获取namespace
    @GetMapping("/chaos-available-namespaces")
    @OperationLog("获取可用命名空间")
    public CustomResult getNamespaces(){
        String url = chaosMeshUrl + "/api/common/chaos-available-namespaces";
        return CustomResult.ok(restTemplate.getForObject(url, String[].class));
    }
    // 根据namespace获取labels
    @GetMapping("/labels")
    @OperationLog("获取标签")
    public CustomResult getLabelsByNamespace(String namespace) throws IOException {
        String url = chaosMeshUrl + "/api/common/labels";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",  chaosMeshToken);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<Object> response = restTemplate.exchange(url + "?podNamespaceList=" + namespace, HttpMethod.GET, entity, Object.class);
        return CustomResult.ok(response.getBody());
    }

    // 根据namespaces与labels获取pods
    @PostMapping("/pods")
    @OperationLog("获取Pod列表")
    public CustomResult getPods(@RequestBody QuestPodBody questPodBody) throws IOException {
        String url = chaosMeshUrl + "/api/common/pods";
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization",  chaosMeshToken);
        // 创建请求实体对象
        HttpEntity<QuestPodBody> requestEntity = new HttpEntity<>(questPodBody, headers);

        // 使用RestTemplate发送POST请求
        ResponseEntity<Object> response = restTemplate.postForEntity(url, requestEntity, Object.class);

        // 根据响应创建并返回CustomResult
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // 根据你的需求转换response.getBody()为CustomResult
            return CustomResult.ok(response.getBody());
        } else {
            return new CustomResult(40000,"获取失败",null);
        }
    }
}

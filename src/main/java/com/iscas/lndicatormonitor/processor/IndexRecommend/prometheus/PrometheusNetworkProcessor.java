package com.iscas.lndicatormonitor.processor.IndexRecommend.prometheus;

import com.iscas.lndicatormonitor.dto.indexRecommend.PrometheusProcessorDTO;
import com.iscas.lndicatormonitor.rule.MetricQueryRuleLibrary;
import com.iscas.lndicatormonitor.utils.NetworkInterfaceResolver;
import com.iscas.lndicatormonitor.utils.PrometheusUtil;
import org.springframework.stereotype.Component;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class PrometheusNetworkProcessor {

    private static final Logger log = LoggerFactory.getLogger(PrometheusNetworkProcessor.class);

    @Autowired
    private NetworkInterfaceResolver networkInterfaceResolver;

    @Autowired
    private MetricQueryRuleLibrary ruleLibrary;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PrometheusUtil prometheusUtil;

    public Object process(PrometheusProcessorDTO dto) {
        log.info("Processing PrometheusProcessorDTO: {}", dto);

        try {
            // Step 1: 初始化参数
            Map<String, String> params = new HashMap<>();
            if (dto.getInstance() == null || dto.getInstance().isEmpty()) {
                // 第一种情况: 处理容器指标
                log.info("Processing container metrics for namespace: {}, podName: {}", dto.getNamespace(), dto.getPodName());

                // 获取网络接口
                String networkInterface = networkInterfaceResolver.getPodNetworkInterface(dto.getNamespace(), dto.getPodName());
                if (networkInterface == null) {
                    log.error("Failed to resolve network interface for namespace: {}, podName: {}", dto.getNamespace(), dto.getPodName());
                    return null;
                }
                log.info("Resolved network interface: {}", networkInterface);

                params.put("interface", networkInterface);
            } else {
                // 第二种情况: 处理节点指标
                log.info("Processing node metrics for instance: {}", dto.getInstance());
                params.put("instance", dto.getInstance());
            }

            // Step 2: 生成查询语句
            String query = ruleLibrary.getQuery(dto.getIndexName(), params);
            query = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            log.info("Generated PromQL query: {}", query);
            // Step 3: 计算步长
            int step = prometheusUtil.calculateStep(new Date(dto.getFrom()), new Date(dto.getTo()));
            log.info("Calculated step: {}", step);

            // Step 4: 构建查询 URL
            String url = prometheusUtil.queryRange(query, (double) dto.getFrom() / 1000, (double) dto.getTo() / 1000, step);
            log.info("Generated Prometheus query URL: {}", url);

            // Step 5: 发送 GET 请求并返回结果
            Object response = restTemplate.getForObject(new URI(url), Object.class);
            return response;
        } catch (Exception e) {
            log.error("Error processing PrometheusProcessorDTO: {}", dto, e);
            return null;
        }
    }
}
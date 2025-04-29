package com.iscas.lndicatormonitor.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iscas.lndicatormonitor.domain.PrometheusIndex;
import com.iscas.lndicatormonitor.service.PrometheusIndexServicenew;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrometheusIndexNewServiceImpl implements PrometheusIndexServicenew {
    @Value("${prometheus.url}")
    private String prometheusUrl;

    @Value("${prometheus.os-type}")
    private String prometheusOSType;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(PrometheusIndexNewServiceImpl.class);

    public Object getPrometheusIndex(PrometheusIndex prometheusIndex) throws Exception {
        logger.info("生成查询字符串：" + prometheusIndex);
        String queryString = generateQueryString(prometheusIndex);
        String url = buildUrl(prometheusUrl, queryString, prometheusIndex.getStartTime(), prometheusIndex.getEndTime(), prometheusIndex.getStep());

        logger.debug("生成的URL：" + url);


        List<Object> responseInfo = executeQuery(url);

        return responseInfo != null ? responseInfo : new JSONArray();
    }

    private String generateQueryString(PrometheusIndex prometheusIndex) throws Exception {
        Map<String, String> queryTemplates = new HashMap<>();
        String namespace = prometheusIndex.getNameSpace();
        String pod = prometheusIndex.getPodName();
        String commonPodPattern = "{namespace=\"" + namespace + "\",pod=~\"" + pod.trim() + "-.*\"}";
        logger.info("commonPodPattern：" + commonPodPattern);
        queryTemplates.put("CPU", "avg(rate(container_cpu_usage_seconds_total" + commonPodPattern + "[1m]))*100");
        queryTemplates.put("Memory", "avg(container_memory_usage_bytes" + commonPodPattern + ") / (1024 * 1024)");
        queryTemplates.put("DiskIO", "sum(irate(container_fs_reads_total" + commonPodPattern + "[1m])) + sum(irate(container_fs_writes_total" + commonPodPattern + "[1m]))");
        queryTemplates.put("receivePackets", "avg(rate(container_network_receive_packets_total" + commonPodPattern + "[1m]))*100");
        queryTemplates.put("transmitPackets", "avg(rate(container_network_transmit_packets_total" + commonPodPattern + "[1m]))*100");
        queryTemplates.put("receiveBandwidth", "avg(rate(container_network_receive_bytes_total" + commonPodPattern + "[1m]))*100");
        queryTemplates.put("transmitBandwidth", "avg(rate(container_network_transmit_bytes_total" + commonPodPattern + "[1m]))*100");
        queryTemplates.put("node_CPU", "100 - (avg(irate(node_cpu_seconds_total{mode=\"idle\"}[1m])) * 100)");
        queryTemplates.put("node_memory", "100 - avg(node_memory_MemAvailable_bytes) / avg(node_memory_MemTotal_bytes)* 100");
        queryTemplates.put("node_network_receive", "sum(rate(node_network_receive_bytes_total{device!=\"lo\"}[1m])) by (instance) * 8");
        queryTemplates.put("node_network_transmit", "rate(node_network_transmit_bytes_total{device=\"eth0\"}[1m]) * 8");
        queryTemplates.put("node_load1", "node_load1");
        queryTemplates.put("node_transmit_packets", "sum(rate(node_network_transmit_packets_total{device!=\"lo\"}[1m])) by (instance)");
        queryTemplates.put("node_receive_packets", "sum(rate(node_network_receive_packets_total{device!=\"lo\"}[1m])) by (instance)");

        String name = prometheusIndex.getName();
        if (queryTemplates.containsKey(name)) {
            logger.info("使用模板查询指标：" + name);
            return queryTemplates.get(name);
        } else if (name.equals("node_DiskIO")) {
            return handleNodeDiskIOQuery(namespace, pod);
        }

        throw new IllegalArgumentException("未知的 Prometheus 指标名称：" + name);
    }

    private String handleNodeDiskIOQuery(String namespace, String pod) {
        logger.info("处理 DiskIO 查询，命名空间：" + namespace + "，Pod：" + pod);
        String baseQueryPattern = "sum(rate(%s{device=~\"(/dev/)?(mmcblk.p.+|nvme.+|rbd.+|sd.+|vd.+|xvd.+|dm-.+|md.+|dasd.+)\"}[1m])) by (instance)";
        String queryStringRead = String.format(baseQueryPattern, "node_disk_read_bytes_total");
        String queryStringWrite = String.format(baseQueryPattern, "node_disk_written_bytes_total");
        String queryStringIOTime = String.format(baseQueryPattern, "node_disk_io_time_seconds_total");

        return String.join("&", queryStringRead, queryStringWrite, queryStringIOTime);
    }

    public String buildUrl(String baseUrl, String query, double startTime, double endTime, int step) throws URISyntaxException, UnsupportedEncodingException {
        // URL encode the query string
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

        // Check OS type and replace '+' with '%20' for non-CentOS systems
        if (!"centos".equalsIgnoreCase(prometheusOSType)) {
            encodedQuery = encodedQuery.replace("+", "%20");
            logger.info("非CentOS系统，使用URL编码后的查询字符串：" + encodedQuery);
        }
        // Construct the query string using string concatenation
        String queryString = "query=" + encodedQuery +
                "&start=" + String.format("%.3f", startTime) +
                "&end=" + String.format("%.3f", endTime) +
                "&step=" + step;

        // Construct the final URI
        URI uri = new URI(baseUrl + "?" + queryString);
        return uri.toString();
    }

    private List<Object> executeQuery(String url) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            logger.info("执行查询：" + url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                logger.debug("响应状态码：" + statusCode);
                String responseData = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(responseData);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray resultArray = data.getJSONArray("result");

                // Initialize list to hold the values
                List<Object> valuesList = new ArrayList<>();

                // Process the result array
                for (int i = 0; i < resultArray.size(); i++) {
                    JSONObject resultElement = resultArray.getJSONObject(i);
                    JSONArray values = resultElement.getJSONArray("values");
                    valuesList.addAll(values);
                }

                return valuesList;
            } catch (Exception e) {
                logger.error("执行查询出错：" + url, e);
                throw e;
            }
        }
    }
    @Override
    public Object getNodeIndex(PrometheusIndex prometheusIndex) throws Exception {
        String baseUrl =prometheusUrl + "/api/v1/query_range";
        String name = prometheusIndex.getName();
        double startTime = prometheusIndex.getStartTime();
        double endTime = prometheusIndex.getEndTime();
        int step = prometheusIndex.getStep();
        String instance = prometheusIndex.getIp();
        String encodedQueryString = "";
        if (name.equals("CPU")) {
            encodedQueryString = URLEncoder.encode(
                    "100 - (avg(irate(node_cpu_seconds_total{instance=\"" + instance + "\", mode=\"idle\"}[1m])) * 100)",
                    "UTF-8"
            ).replace("+", "%20");
        }else if (name.equals("Memory")){
            encodedQueryString = URLEncoder.encode(
                    "(1 - (avg(node_memory_MemFree_bytes{instance=\"" + instance + "\"} + " +
                            "node_memory_Cached_bytes + node_memory_Buffers_bytes{instance=\"" + instance + "\"}) / " +
                            "avg(node_memory_MemTotal_bytes{instance=\"" + instance + "\"}))) * 100",
                    "UTF-8"
            ).replace("+", "%20");
        }else if (name.equals("DiskIO")){
            String encodedQueryStringRead = URLEncoder.encode("rate(node_disk_read_bytes_total{instance=\"" + instance + "\", device=~\"(/dev/)?(mmcblk.p.+|nvme.+|rbd.+|sd.+|vd.+|xvd.+|dm-.+|md.+|dasd.+)\"}[1m])", "UTF-8").replace("+", "%20");


            String encodedQueryStringWrite = URLEncoder.encode("rate(node_disk_written_bytes_total{instance=\"" + instance + "\", device=~\"(/dev/)?(mmcblk.p.+|nvme.+|rbd.+|sd.+|vd.+|xvd.+|dm-.+|md.+|dasd.+)\"}[1m])", "UTF-8").replace("+", "%20");


            String encodedQueryStringIOTime = URLEncoder.encode("rate(node_disk_io_time_seconds_total{instance=\"" + instance + "\", device=~\"(/dev/)?(mmcblk.p.+|nvme.+|rbd.+|sd.+|vd.+|xvd.+|dm-.+|md.+|dasd.+)\"}[1m])", "UTF-8").replace("+", "%20");

            String read = String.format("%s?query=%s&start=%.3f&end=%.3f&step=%d",
                    baseUrl, encodedQueryStringRead, startTime, endTime, step);

            String write = String.format("%s?query=%s&start=%.3f&end=%.3f&step=%d",
                    baseUrl, encodedQueryStringWrite, startTime, endTime, step);

            String IOTime = String.format("%s?query=%s&start=%.3f&end=%.3f&step=%d",
                    baseUrl, encodedQueryStringIOTime, startTime, endTime, step);

            CloseableHttpClient httpClient = HttpClients.createDefault();

            Map<JSONObject, Object> info = new HashMap<>();
            try {
                CloseableHttpResponse responseRead = httpClient.execute(new HttpGet(read));
                try {
                    String responseBodyRead = EntityUtils.toString(responseRead.getEntity());
                    JSONObject jsonObject = JSON.parseObject(responseBodyRead);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray resultArray = data.getJSONArray("result");
                    for(int i = 0 ; i < resultArray.size(); i ++) {
                        JSONObject firstElement = resultArray.getJSONObject(i);
                        JSONObject metric1 = firstElement.getJSONObject("metric");
                        Object device1 = metric1.get("device");
                        metric1.put(device1 + "index", device1 + " write");
                        JSONArray value1 = firstElement.getJSONArray("values");
                        info.put(metric1, value1);
                    }
                } finally {
                    responseRead.close();
                }

                // 执行第二个请求，获取磁盘写入数据
                CloseableHttpResponse responseWrite = httpClient.execute(new HttpGet(write));
                try {
                    // 处理磁盘写入的响应
                    String responseBodyWrite = EntityUtils.toString(responseWrite.getEntity());
                    JSONObject jsonObject = JSON.parseObject(responseBodyWrite);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray resultArray = data.getJSONArray("result");
                    for(int i = 0 ; i < resultArray.size(); i ++) {
                        JSONObject firstElement = resultArray.getJSONObject(i);
                        JSONObject metric2 = firstElement.getJSONObject("metric");
                        Object device2 = metric2.get("device");
                        metric2.put(device2 + "index", device2 + " write");
                        JSONArray value2 = firstElement.getJSONArray("values");
                        info.put(metric2, value2);
                    }
                } finally {
                    responseWrite.close();
                }

                // 执行第三个请求，获取磁盘 I/O 时间数据
                CloseableHttpResponse responseIOTime = httpClient.execute(new HttpGet(IOTime));
                try {
                    String responseBodyIOTime = EntityUtils.toString(responseIOTime.getEntity());
                    JSONObject jsonObject = JSON.parseObject(responseBodyIOTime);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray resultArray = data.getJSONArray("result");
                    for(int i = 0 ; i < resultArray.size(); i ++) {
                        JSONObject firstElement = resultArray.getJSONObject(i);
                        JSONObject metric3 = firstElement.getJSONObject("metric");
                        Object device3 = metric3.get("device");
                        metric3.put(device3 + "index", device3 + " write");
                        JSONArray value3 = firstElement.getJSONArray("values");
                        info.put(metric3, value3);
                    }
                } finally {
                    responseIOTime.close();
                }

            } finally {
                // 关闭 HttpClient
                httpClient.close();
            }

            return info;

        }else if (name.equals("receivePackets")){
        }else if (name.equals("transmitPackets")) {
        }else if (name.equals("receiveBandwidth")){
        }else if (name.equals("transmitBandwidth")) {
        }

        String url = String.format("%s?query=%s&start=%.3f&end=%.3f&step=%d",
                baseUrl, encodedQueryString, startTime, endTime, step);
        HttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);

        HttpResponse response = httpClient.execute(httpGet);

        int statusCode = response.getStatusLine().getStatusCode();

        HttpEntity entity = response.getEntity();
        String responseData = EntityUtils.toString(entity);

        JSONObject jsonObject = JSON.parseObject(responseData);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray resultArray = data.getJSONArray("result");
        JSONObject firstElement = resultArray.getJSONObject(0);
        JSONObject metric = firstElement.getJSONObject("metric");
        JSONArray values = firstElement.getJSONArray("values");
        return values;
    }

}

package com.iscas.lndicatormonitor.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iscas.lndicatormonitor.domain.PrometheusIndex;
import com.iscas.lndicatormonitor.service.PrometheusIndexService;
import com.iscas.lndicatormonitor.utils.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Yukun Hou
 * @create 2023-10-10 15:38
 */

@Service
public class PrometheusIndexServiceImpl implements PrometheusIndexService {
    @Value("${prometheus.url}")
    private String prometheusUrl;

    @Override
    public Object getPrometheusIndex(PrometheusIndex prometheusIndex) throws Exception {
            String baseUrl = prometheusUrl + "/api/v1/query_range";
            String name = prometheusIndex.getName();
            String namespace = prometheusIndex.getNameSpace();
            String pod = prometheusIndex.getPodName();
            double startTime = prometheusIndex.getStartTime();
            double endTime = prometheusIndex.getEndTime();
            int step = prometheusIndex.getStep();
            String encodedQueryString = "";
            if (name.equals("CPU")) {
                    encodedQueryString = URLEncoder.encode("sum(rate(container_cpu_usage_seconds_total{namespace=\"" + namespace + "\",pod=\"" + pod + "\"}[1m]))*100", "UTF-8").replace("+", "%20");
            }else if (name.equals("Memory")){
                    encodedQueryString = URLEncoder.encode("100 * sum(container_memory_usage_bytes{namespace=\"" + namespace + "\",pod=\"" + pod + "\"}) / sum(container_spec_memory_limit_bytes{namespace=\"" + namespace + "\",pod=\"" + pod + "\"})","UTF-8").replace("+", "%20");
            }else if (name.equals("DiskIO")){
                    encodedQueryString="irate%28container_fs_reads_bytes_total%7Bnamespace%3D%22" + namespace +"%22%2Cpod%3D%22" + pod + "%22%7D%5B1m%5D%29%0A%2B%0Airate%28container_fs_writes_bytes_total%7Bnamespace%3D%22" + namespace +"%22%2C+pod%3D%22" + pod + "%22%7D%5B1m%5D%29";
            }else if (name.equals("receivePackets")){
                    encodedQueryString = URLEncoder.encode("rate(container_network_receive_packets_total{namespace=\"" + namespace + "\",pod=\"" + pod + "\"}[1m])*100", "UTF-8").replace("+", "%20");
            }else if (name.equals("transmitPackets")) {
                    encodedQueryString = URLEncoder.encode("rate(container_network_transmit_packets_total{namespace=\"" + namespace + "\",pod=\"" + pod + "\"}[1m])*100", "UTF-8").replace("+", "%20");
            }else if (name.equals("receiveBandwidth")){
                    encodedQueryString = URLEncoder.encode("rate(container_network_receive_bytes_total{namespace=\"" + namespace + "\",pod=\"" + pod + "\"}[1m])*100", "UTF-8").replace("+", "%20");
            }else if (name.equals("transmitBandwidth")) {
                    encodedQueryString = URLEncoder.encode("rate(container_network_transmit_bytes_total{namespace=\"" + namespace + "\",pod=\"" + pod + "\"}[1m])*100", "UTF-8").replace("+", "%20");
            }else if (name.equals("node_CPU")) {
                encodedQueryString = URLEncoder.encode(
                        "100 - (avg(irate(node_cpu_seconds_total{mode=\"idle\"}[1m])) * 100)",
                        "UTF-8"
                ).replace("+", "%20");
            }else if (name.equals("node_memory")) {
                encodedQueryString = URLEncoder.encode(
                        "100 - avg(node_memory_MemAvailable_bytes) / avg(node_memory_MemTotal_bytes)* 100",
                        "UTF-8"
                ).replace("+", "%20");
            }else if (name.equals("node_DiskIO")) {
                String encodedQueryStringRead = URLEncoder.encode("rate(node_disk_read_bytes_total{device=~\"(/dev/)?(mmcblk.p.+|nvme.+|rbd.+|sd.+|vd.+|xvd.+|dm-.+|md.+|dasd.+)\"}[1m])", "UTF-8").replace("+", "%20");

                String encodedQueryStringWrite = URLEncoder.encode("rate(node_disk_written_bytes_total{device=~\"(/dev/)?(mmcblk.p.+|nvme.+|rbd.+|sd.+|vd.+|xvd.+|dm-.+|md.+|dasd.+)\"}[1m])", "UTF-8").replace("+", "%20");

                String encodedQueryStringIOTime = URLEncoder.encode("rate(node_disk_io_time_seconds_total{device=~\"(/dev/)?(mmcblk.p.+|nvme.+|rbd.+|sd.+|vd.+|xvd.+|dm-.+|md.+|dasd.+)\"}[1m])", "UTF-8").replace("+", "%20");

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
                            metric1.put(device1 + "index", device1 + " read");
                            JSONArray value1 = firstElement.getJSONArray("values");
                            info.put(metric1, value1);
                        }
                    } finally {
                        responseRead.close();
                    }

                    CloseableHttpResponse responseWrite = httpClient.execute(new HttpGet(write));
                    try {
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
                            metric3.put(device3 + "index", device3 + " i/o time");
                            JSONArray value3 = firstElement.getJSONArray("values");
                            info.put(metric3, value3);
                        }
                    } finally {
                        responseIOTime.close();
                    }

                } finally {
                    httpClient.close();
                }

                return info;
            }else if (name.equals("node_network_receive")) {
                encodedQueryString = URLEncoder.encode(
                        "rate(node_network_receive_bytes_total{device!=\"lo\"}[1m]) * 8",
                        "UTF-8"
                ).replace("+", "%20");
            }else if (name.equals("node_network_transmit")) {
                encodedQueryString = URLEncoder.encode(
                        "rate(node_network_transmit_bytes_total{device=\"lo\"}[1m])  * 8",
                        "UTF-8"
                ).replace("+", "%20");
            }else if (name.equals("node_load1")) {
                encodedQueryString = URLEncoder.encode(
                        "node_load1",
                        "UTF-8"
                ).replace("+", "%20");
            }else if (name.equals("node_transmit_packets")) {
                encodedQueryString = URLEncoder.encode(
                        "rate(node_network_transmit_packets_total{device!=\"lo\"}[1m])",
                        "UTF-8"
                ).replace("+", "%20");
            }else if (name.equals("node_receive_packets")) {
                encodedQueryString = URLEncoder.encode(
                        "rate(node_network_receive_packets_total{device!=\"lo\"}[1m])",
                        "UTF-8"
                ).replace("+", "%20");
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
            if (name.equals("node_network_receive") || name.equals("node_DiskIO") || name.equals("node_transmit_packets") || name.equals("node_receive_packets") || name.equals("node_network_transmit")){
                Map<JSONObject, Object> node_ReceivePackets_info = new HashMap<>();
                for(int i = 0 ; i < resultArray.size(); i ++) {
                    JSONObject firstElement = resultArray.getJSONObject(i);
                    JSONObject metric3 = firstElement.getJSONObject("metric");
                    JSONArray value3 = firstElement.getJSONArray("values");
                    node_ReceivePackets_info.put(metric3, value3);
                }
                return node_ReceivePackets_info;
            }
            JSONObject firstElement = resultArray.getJSONObject(0);
            JSONObject metric = firstElement.getJSONObject("metric");
            JSONArray values = firstElement.getJSONArray("values");
            return values;
    }

        @Override
        public Object getNodeIndex(PrometheusIndex prometheusIndex) throws Exception {
        String baseUrl = prometheusUrl + "/api/v1/query_range";
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
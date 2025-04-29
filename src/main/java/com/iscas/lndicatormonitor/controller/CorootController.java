package com.iscas.lndicatormonitor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Report;
import com.iscas.lndicatormonitor.dto.coroot.MetricResponse;
import com.iscas.lndicatormonitor.dto.coroot.TraceRequestFilter;
import com.iscas.lndicatormonitor.dto.coroot.TraceRequestQuery;
import com.iscas.lndicatormonitor.dto.coroot.TraceResponse;
import com.iscas.lndicatormonitor.dto.trace.TraceRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = "/rcadata")
@Slf4j
public class CorootController {

    private static final Logger logger = Logger.getLogger(CorootController.class);
    private ObjectMapper mapper = new ObjectMapper();


    @Value("${coroot.url}")
    private String corootUrl;

    @Value("${coroot.cookie}")
    private String cookie;

    @Value("${coroot.projectId}")
    private String projectId;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getTraceData")
    @OperationLog("获取追踪数据")
    public CustomResult getTraceData(@RequestParam("timestamp") long timestamp, @RequestParam("url") String url, @RequestParam("method") String method) {
        logger.info("Received request to get trace data with timestamp: " + timestamp + ", url: " + url + ", method: " + method);

        String getAllUrl = corootUrl + "/overview/traces";
        String filter_span_name = method + " " + url;
        String serviceName = "";
        JsonNode stats = null;
        ResponseEntity<String> response_err = null;

        mapper = new ObjectMapper();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(getAllUrl + "?query=&from=now-1h", String.class);
            response_err = response;
            mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            stats = root.at("/data/traces/summary/stats");
            if (stats.isArray()) {
                for (JsonNode stat : stats) {
                    if (filter_span_name.equals(stat.get("span_name").asText())) {
                        serviceName = stat.get("service_name").asText();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to get service name", e);
            return CustomResult.fail("Failed to get service name", response_err);
        }

        TraceRequestQuery traceRequestQuery = new TraceRequestQuery();
        traceRequestQuery.setView("traces");

        TraceRequestFilter traceFilter1 = new TraceRequestFilter();
        traceFilter1.setField("ServiceName");
        traceFilter1.setOp("=");
        traceFilter1.setValue(serviceName);

        TraceRequestFilter traceFilter2 = new TraceRequestFilter();
        traceFilter2.setField("SpanName");
        traceFilter2.setOp("=");
        traceFilter2.setValue(filter_span_name);

        traceRequestQuery.setFilters(Arrays.asList(traceFilter1, traceFilter2));
        String query;
        try {
            query = mapper.writeValueAsString(traceRequestQuery);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert filters to json", e);
            return CustomResult.fail("Failed to convert filters to json");
        }

        String requestUrl = getAllUrl + query;
        List<Object> traceList = new ArrayList<>();
        try {
            // 直接使用预先构建好的字符串

            String queryJson = mapper.writeValueAsString(traceRequestQuery);

            String encodedQuery = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());
            requestUrl = getAllUrl + "?query=" + encodedQuery + "&from=now-1h";
            ResponseEntity<String> response = restTemplate.getForEntity(new URI(requestUrl), String.class);
            System.out.println("requestUrl: " + requestUrl);

            JsonNode root = mapper.readTree(response.getBody());
            JsonNode traces = root.at("/data/traces/traces");
            System.out.println("len: " + traces.size());
            if (traces.isArray()) {
                for (JsonNode trace : traces) {
                    System.out.println(trace.get("timestamp").asLong());
                    if (trace.get("timestamp").asLong() == timestamp) {
                        traceList.add(trace);
                    }
                }
            }
            logger.info("Number of traces matching timestamp: " + traceList.size());
        } catch (Exception e) {
            logger.error("Failed to get trace data", e);
            return CustomResult.fail("Failed to get trace data", requestUrl);
        }

        List<Object> rootErrorSpanList = new ArrayList<>();
        ArrayList<Object> targetTraceList = new ArrayList<>();

        for (Object traceObj : traceList) {
            JsonNode trace = (JsonNode) traceObj;
            String traceId = trace.get("trace_id").asText();
            traceRequestQuery.setTraceId(traceId);
            try {
                mapper = new ObjectMapper();
                String queryJson = mapper.writeValueAsString(traceRequestQuery);
                String encodedQuery = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());
                requestUrl = getAllUrl + "?query=" + encodedQuery + "&from=now-1h";
                System.out.println("requestUrl: " + requestUrl);
                ResponseEntity<String> response = restTemplate.getForEntity(new URI(requestUrl), String.class);
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode traceDetails = root.at("/data/traces/trace");
                // 移除指定字段
                JsonNode cloneTraceDetails = traceDetails;

                // 如果是 ArrayNode 类型，遍历每个元素
                ArrayNode arrayNode = (ArrayNode) cloneTraceDetails;
                arrayNode.forEach(node -> {
                    if (node.isObject()) {
                        ObjectNode objectNode = (ObjectNode) node;
                        objectNode.remove("attributes");
                        objectNode.remove("details");
                    }
                });

                targetTraceList.add(cloneTraceDetails);

                if (traceDetails.isArray()) {
                    Set<String> parentIds = new HashSet<>();
                    for (JsonNode span : traceDetails) {
                        JsonNode parentIdNode = span.get("parent_id");
                        if (parentIdNode != null && !parentIdNode.isNull()) {
                            parentIds.add(parentIdNode.asText());
                        }
                    }
                    for (JsonNode span : traceDetails) {
                        String spanId = span.get("id").asText();
                        boolean isRootError = span.get("status").get("error").asBoolean() && !parentIds.contains(spanId);
                        if (isRootError) {
                            rootErrorSpanList.add(span);
                        }
                    }
                }
                logger.info("Trace ID: " + traceId + " processed successfully.");
            } catch (Exception e) {
                logger.error("Failed to get root error span list for Trace ID: " + traceId, e);
                return CustomResult.fail("Failed to get root error span list");
            }
        }

        TraceResponse traceResponse = new TraceResponse();
        traceResponse.setTraceList(targetTraceList);
        traceResponse.setRooterrorSpanList(rootErrorSpanList);

        logger.info("Trace data response constructed successfully.");
        return CustomResult.ok(Collections.singletonList(traceResponse));
    }

    // 根据TraceId获取trace数据
    @GetMapping(value = "/getTraceDataByTraceId")
    @OperationLog("根据TraceId获取追踪数据")
    public CustomResult getTraceDataByTraceId(@RequestParam("traceId") String traceId) throws JsonProcessingException, UnsupportedEncodingException, URISyntaxException {
        String getTraceUrl = corootUrl + "/overview/traces";
        TraceRequestQuery traceRequestQuery = new TraceRequestQuery();
        traceRequestQuery.setView("traces");
        traceRequestQuery.setTraceId(traceId);
        String queryJson = mapper.writeValueAsString(traceRequestQuery);

        String encodedQuery = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());
        String requestUrl = getTraceUrl + "?query=" + encodedQuery;
        ResponseEntity<String> response = restTemplate.getForEntity(new URI(requestUrl), String.class);
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode traceDetails = root.at("/data/traces/trace");

        List<Object> rootErrorSpanList = new ArrayList<>();
        ArrayList<Object> targetTraceList = new ArrayList<>();
        // 移除指定字段
        JsonNode cloneTraceDetails = traceDetails;

        // 如果是 ArrayNode 类型，遍历每个元素
        ArrayNode arrayNode = (ArrayNode) cloneTraceDetails;
        arrayNode.forEach(node -> {
            if (node.isObject()) {
                ObjectNode objectNode = (ObjectNode) node;
                objectNode.remove("attributes");
                objectNode.remove("details");
            }
        });
        targetTraceList.add(cloneTraceDetails);
        if (traceDetails.isArray()) {
            Set<String> parentIds = new HashSet<>();
            for (JsonNode span : traceDetails) {
                JsonNode parentIdNode = span.get("parent_id");
                if (parentIdNode != null && !parentIdNode.isNull()) {
                    parentIds.add(parentIdNode.asText());
                }
            }
            for (JsonNode span : traceDetails) {
                String spanId = span.get("id").asText();
                boolean isRootError = span.get("status").get("error").asBoolean() && !span.get("events").isEmpty();
                if (isRootError) {
                    rootErrorSpanList.add(span);
                }
            }
        }
        logger.info("Trace ID: " + traceId + " processed successfully.");
        TraceResponse traceResponse = new TraceResponse();
        traceResponse.setTraceList(targetTraceList);
        traceResponse.setRooterrorSpanList(rootErrorSpanList);

        logger.info("Trace data response constructed successfully.");
        return CustomResult.ok(Collections.singletonList(traceResponse));
    }

    @GetMapping("/getMetricOverview")
    @OperationLog("获取指标概览")
    public CustomResult getMetricOverview(
            @RequestParam("startTime") long startTime,
            @RequestParam("endTime") long endTime,
            @RequestParam("metricPrefix") String metricPrefix,
//            @RequestParam("metricSuffix") String metricSuffix,
            @RequestParam("serviceName") String serviceName,
            @RequestParam("namespace") String namespace) {

        String[] serviceNameSplit = serviceName.split("-");
        for (int i = 0; i < serviceNameSplit.length; i++) {
            String reducedServiceName = String.join("-", Arrays.copyOf(serviceNameSplit, serviceNameSplit.length - i));

            String url = corootUrl +  projectId+ "/app/" +
                    namespace + ":Deployment:" + reducedServiceName +
                    "?from=" + startTime + "&to=" + endTime;

            try {
                logger.info("Sending GET request to external URL: " + url);
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("Successfully retrieved data from external service");
                    return processMetrics(response.getBody(), metricPrefix, startTime, endTime);
                } else {
                    logger.error("Failed to retrieve data from external service, status code: " + response.getStatusCode());
                    return CustomResult.fail("Failed to retrieve data");
                }
            } catch (Exception e) {
                logger.error("Error occurred while fetching metric data: " + null, e);

            }
        }
        return CustomResult.fail("Error fetching metric data");
    }


    @GetMapping("/getNodeMetricOverview")
    @OperationLog("获取节点指标概览")
    public CustomResult getNodeMetricOverview(
            @RequestParam("startTime") long startTime,
            @RequestParam("endTime") long endTime,
            @RequestParam("metricPrefix") String metricPrefix,
//            @RequestParam("metricSuffix") String metricSuffix,
            @RequestParam("nodeName") String nodeName
    ) {

        String url = corootUrl + projectId + "/node/" + nodeName +
                "?from=" + startTime + "&to=" + endTime;

        try {
            logger.info("Sending GET request to external URL: " + url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully retrieved data from external service");
                return processNodeMetrics(response.getBody(), metricPrefix, startTime, endTime);
            } else {
                logger.error("Failed to retrieve data from external service, status code: " + response.getStatusCode());
                return CustomResult.fail("Failed to retrieve data");
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching metric data: " + null, e);
            return CustomResult.fail("Error fetching metric data");
        }
    }

    private CustomResult processMetrics(String responseBody, String metricPrefix, long startTime, long endTime) {
        List<MetricResponse> metricResponses = new ArrayList<>();

        try {
            logger.info("Processing metrics data with metricPrefix: " + metricPrefix);
            JsonNode root = mapper.readTree(responseBody);
            JsonNode reports = root.at("/data/reports");

            if (reports.isArray()) {
                logger.info("Found " + reports.size() + " reports in the response data.");

                for (JsonNode report : reports) {
                    String reportName = report.get("name").asText().toLowerCase();
                    logger.info("Processing report: " + reportName);

                    // 判断报告类型：CPU、Memory、Net、DNS 或 JVM
                    if (isMatchingReport(reportName, metricPrefix)) {
                        JsonNode widgets = report.get("widgets");
                        String lowerCaseMetricPrefix = metricPrefix.toLowerCase();
                        if (widgets.isArray()) {
                            logger.info("Found " + widgets.size() + " widgets in report " + reportName);

                            for (JsonNode widget : widgets) {
                                switch (lowerCaseMetricPrefix) {
                                    case "cpu":
                                        processCpuOrMemoryMetrics(widget, "usage", metricResponses, startTime, endTime);
                                        processCpuOrMemoryMetrics(widget, "delay", metricResponses, startTime, endTime);
                                        processCpuOrMemoryMetrics(widget, "throttled_time", metricResponses, startTime, endTime);
                                        break;
                                    case "memory":
                                        processCpuOrMemoryMetrics(widget, "usage", metricResponses, startTime, endTime);
                                        processCpuOrMemoryMetrics(widget, "consumers", metricResponses, startTime, endTime);
                                        break;
                                    case "net":
                                        processNetMetrics(widget, "TCP_connection_latency".replace("_", " "), metricResponses, startTime, endTime);
                                        processNetMetrics(widget, "Active_TCP_connections".replace("_", " "), metricResponses, startTime, endTime);
                                        processNetMetrics(widget, "TCP_connection_attempts".replace("_", " "), metricResponses, startTime, endTime);
                                        break;
                                    case "dns":
                                        processDnsMetrics(widget, "requests_by_type".replace("_", " "), metricResponses, startTime, endTime);
                                        processDnsMetrics(widget, "errors".replace("_", " "), metricResponses, startTime, endTime);
                                        processDnsMetrics(widget, "latency".replace("_", " "), metricResponses, startTime, endTime);

                                        break;
                                    case "jvm":
                                        processJvmMetrics(widget, "Heap_size".replace("_", " "), metricResponses, startTime, endTime);
                                        processJvmMetrics(widget, "GC_time".replace("_", " "), metricResponses, startTime, endTime);
                                        processJvmMetrics(widget, "Safepoint_time".replace("_", " "), metricResponses, startTime, endTime);
                                        break;
                                }
                            }
                        }
                    } else {
                        logger.info("Skipping report " + reportName + " as it does not match metricPrefix: " + metricPrefix);
                    }
                }
            } else {
                logger.warn("No reports found in the response data.");
            }
        } catch (Exception e) {
            logger.error("Error processing metrics data: " + null, e);
            return CustomResult.fail("Error processing metrics data");
        }

        logger.info("Metrics processing complete, returning results with " + metricResponses.size() + " metric responses");
        return CustomResult.ok(metricResponses);
    }


    private CustomResult processNodeMetrics(String responseBody, String metricPrefix, long startTime, long endTime) {
        List<MetricResponse> metricResponses = new ArrayList<>();

        try {
            logger.info("Processing metrics data with metricPrefix: " + metricPrefix);
            JsonNode root = mapper.readTree(responseBody);
            JsonNode data = root.at("/data");


            logger.info("Found " + data.size() + " reports in the response data.");


            String reportName = data.get("name").asText().toLowerCase();
            logger.info("Processing report: " + reportName);

            // 判断报告类型：CPU、Memory、Net、DNS 或 JVM
            JsonNode widgets = data.get("widgets");
            String lowerCaseMetricPrefix = metricPrefix.toLowerCase();
            if (widgets.isArray()) {
                logger.info("Found " + widgets.size() + " widgets in report " + reportName);

                for (JsonNode widget : widgets) {
//                    processNodeMetrics(widget, metricResponses, startTime, endTime);

                    switch (lowerCaseMetricPrefix) {
                        case "cpu":
                        case "memory":
                            processNodeMetrics(widget, lowerCaseMetricPrefix, metricResponses, startTime, endTime);
                            break;
                        case "network":
                        case "disk":
                        case "i/o":
                            processNodeNetMetrics(widget, lowerCaseMetricPrefix, metricResponses, startTime, endTime);
                            break;

                    }
                }
            }


        } catch (Exception e) {
            logger.error("Error processing metrics data: " + null, e);
            return CustomResult.fail("Error processing metrics data");
        }

        logger.info("Metrics processing complete, returning results with " + metricResponses.size() + " metric responses");
        return CustomResult.ok(metricResponses);
    }


    private boolean isMatchingReport(String reportName, String metricPrefix) {
        return reportName.contains(metricPrefix.toLowerCase());
    }

    private void processCpuOrMemoryMetrics(JsonNode widget, String metricSuffix, List<MetricResponse> metricResponses, long startTime, long endTime) {
        JsonNode docLink = widget.at("/doc_link");
        String item = docLink.get("item").asText();
        String hash = docLink.get("hash").asText();
        logger.info("Processing widget with docLink item: " + item + ", hash: " + hash);

        JsonNode charts = widget.at("/chart_group/charts");

        // 确保 charts 是数组并遍历其中的每个图表
        if (charts.isArray()) {
            for (JsonNode chart : charts) {
                if (item.equalsIgnoreCase("cpu") && hash.equalsIgnoreCase(metricSuffix)) {
                    processChartData(chart, "CPU " + metricSuffix, metricResponses, startTime, endTime);
                } else if (item.equalsIgnoreCase("memory") && hash.equalsIgnoreCase(metricSuffix)) {
                    processChartData(chart, "Memory " + metricSuffix, metricResponses, startTime, endTime);
                }
            }
        } else {
            logger.warn("Expected charts to be an array but found different structure in widget with item: " + item);
        }
    }

    private void processNodeMetrics(JsonNode widget, String processNodeMetrics, List<MetricResponse> metricResponses, long startTime, long endTime) {


        if (widget.at("/doc_link") == null) {
            return;
        }

        JsonNode docLink = widget.at("/doc_link");
        if (docLink.get("item") == null || !processNodeMetrics.equals(docLink.get("item").asText())) {
            return;
        }

        String item = docLink.get("item").asText();
        String hash = docLink.get("hash").asText();
        logger.info("Processing widget with docLink item: " + item + ", hash: " + hash);

        JsonNode chart = widget.at("/chart");
        processNodeChartData(chart, item.toUpperCase(), metricResponses, startTime, endTime);
//        if (item.equalsIgnoreCase("cpu")) {
//            processNodeChartData(chart, "CPU ", metricResponses, startTime, endTime);
//        } else if (item.equalsIgnoreCase("memory")) {
//            processNodeChartData(chart, "Memory", metricResponses, startTime, endTime);
//        }
    }

    private void processNetMetrics(JsonNode widget, String metricSuffix, List<MetricResponse> metricResponses, long startTime, long endTime) {
        JsonNode chart_box = null;
        boolean is_chart_group = false;
        if (widget.has("chart_group")) {
            chart_box = widget.at("/chart_group");
            is_chart_group = true;
        } else {
            chart_box = widget.at("/chart");
        }

        String title = chart_box.has("title") ? chart_box.get("title").asText() : "null";
        if ((metricSuffix.equalsIgnoreCase("round-trip time") && title.contains("round-trip")) ||
                (metricSuffix.equalsIgnoreCase("TCP connection latency") && title.contains("latency")) ||
                (metricSuffix.equalsIgnoreCase("Active TCP connections") && title.contains("Active TCP connections")) ||
                (metricSuffix.equalsIgnoreCase("TCP connection attempts") && title.contains("TCP connection attempts"))) {
            JsonNode charts = is_chart_group ? chart_box.at("/charts") : chart_box;
            if (charts.isArray() && is_chart_group) {
                for (JsonNode chart : charts) {
                    processChartData(chart, "Net " + metricSuffix, metricResponses, startTime, endTime);
                }
            } else {
                processChartData(charts, "Net " + metricSuffix, metricResponses, startTime, endTime);
            }
        }

    }

    private void processNodeNetMetrics(JsonNode widget, String metricSuffix, List<MetricResponse> metricResponses, long startTime, long endTime) {
        JsonNode chart_box = null;
        boolean is_chart_group = false;
        if (!widget.has("chart_group")) {
            return;
        }
        chart_box = widget.at("/chart_group");
        String title = chart_box.has("title") ? chart_box.get("title").asText() : "null";
        if (!title.toLowerCase().contains(metricSuffix.toLowerCase())) {
            return;
        }
        JsonNode charts = chart_box.at("/charts");
//        JsonNode charts = chart_box.isArray() ? chart_box.at("/charts") : chart_box;
        if (charts.isArray()){
            for (JsonNode chart : charts) {
                processNodeChartData(chart, title, metricResponses, startTime, endTime);
            }
        }

//        processNodeChartData()
//        if ((metricSuffix.equalsIgnoreCase("round-trip time") && title.contains("round-trip")) ||
//                (metricSuffix.equalsIgnoreCase("TCP connection latency") && title.contains("latency")) ||
//                (metricSuffix.equalsIgnoreCase("Active TCP connections") && title.contains("Active TCP connections")) ||
//                (metricSuffix.equalsIgnoreCase("TCP connection attempts") && title.contains("TCP connection attempts"))) {
//            JsonNode charts = is_chart_group ? chart_box.at("/charts") : chart_box;
//            if (charts.isArray() && is_chart_group) {
//                for (JsonNode chart : charts) {
//                    processChartData(chart, "Net " + metricSuffix, metricResponses, startTime, endTime);
//                }
//            } else {
//                processChartData(charts, "Net " + metricSuffix, metricResponses, startTime, endTime);
//            }
//        }

    }

    private void processDnsMetrics(JsonNode widget, String metricSuffix, List<MetricResponse> metricResponses, long startTime, long endTime) {
        JsonNode chart_box;
        boolean is_chart_group = false;

        // 检查 widget 是否包含 chart_group 或 chart
        if (widget.has("chart_group")) {
            chart_box = widget.at("/chart_group");
            is_chart_group = true;
        } else {
            chart_box = widget.at("/chart");
        }

        String title = chart_box.has("title") ? chart_box.get("title").asText() : "null";
        logger.info("Processing DNS chart with title: " + title);

        // 根据 metricSuffix 和标题内容进行匹配
        if ((metricSuffix.equalsIgnoreCase("requests by type") && title.contains("requests by type")) ||
                (metricSuffix.equalsIgnoreCase("errors") && title.contains("errors")) ||
                (metricSuffix.equalsIgnoreCase("latency") && title.contains("latency"))) {

            JsonNode charts = is_chart_group ? chart_box.at("/charts") : chart_box;
            if (charts.isArray() && is_chart_group) {
                for (JsonNode chart : charts) {
                    processChartData(chart, "DNS " + metricSuffix, metricResponses, startTime, endTime);
                }
            } else {
                processChartData(charts, "DNS " + metricSuffix, metricResponses, startTime, endTime);
            }
        }
    }

    private void processJvmMetrics(JsonNode widget, String metricSuffix, List<MetricResponse> metricResponses, long startTime, long endTime) {
        JsonNode chart_box;
        boolean is_chart_group = false;

        // 检查 widget 是否包含 chart_group 或 chart
        if (widget.has("chart_group")) {
            chart_box = widget.at("/chart_group");
            is_chart_group = true;
        } else {
            chart_box = widget.at("/chart");
        }

        String title = chart_box.has("title") ? chart_box.get("title").asText() : "null";
        logger.info("Processing JVM chart with title: " + title);

        // 根据 metricSuffix 和标题内容进行匹配
        if ((metricSuffix.equalsIgnoreCase("Heap size") && title.contains("Heap size")) ||
                (metricSuffix.equalsIgnoreCase("GC time") && title.contains("GC time")) ||
                (metricSuffix.equalsIgnoreCase("Safepoint time") && title.contains("Safepoint time"))) {

            JsonNode charts = is_chart_group ? chart_box.at("/charts") : chart_box;
            if (charts.isArray() && is_chart_group) {
                for (JsonNode chart : charts) {
                    processChartData(chart, "JVM " + metricSuffix, metricResponses, startTime, endTime);
                }
            } else {
                processChartData(charts, "JVM " + metricSuffix, metricResponses, startTime, endTime);
            }
        }
    }

    private void processChartData(JsonNode chartNode, String metricName, List<MetricResponse> metricResponses, long startTime, long endTime) {
        JsonNode seriesArray = chartNode.at("/series");

        logger.info("seriesArray: " + seriesArray);
        if (seriesArray.isArray()) {
            logger.info("Found " + seriesArray.size() + " series in chart for metric: " + metricName);

            for (JsonNode series : seriesArray) {
                String ownerName = chartNode.get("title").asText();
                JsonNode dataArray = series.get("data");
                logger.info("Processing series for ownerName: " + ownerName + " with data points count: " + dataArray.size());

                double[] dataValues = convertJsonArrayToDoubleArray(dataArray);
                String reportText = analyzeMetric(metricName, dataValues, startTime, endTime);
                logger.info("Generated report for metric " + metricName + " for owner " + ownerName);

                MetricResponse metricResponse = new MetricResponse();
                metricResponse.setOwnerName(ownerName);
//                metricResponse.setMetricValue(convertJsonArrayToList(dataArray));
                metricResponse.setReport(new StringBuilder(reportText));
                metricResponses.add(metricResponse);
                logger.info("Added MetricResponse for owner: " + ownerName);
            }
        } else {
            logger.warn("No series data found in chart for metric: " + metricName);
        }
    }

    private void processNodeChartData(JsonNode chartNode, String metricName, List<MetricResponse> metricResponses, long startTime, long endTime) {
        JsonNode seriesArray = chartNode.at("/series");

        logger.info("seriesArray: " + seriesArray);
        if (seriesArray.isArray()) {
            logger.info("Found " + seriesArray.size() + " series in chart for metric: " + metricName);

            for (JsonNode series : seriesArray) {
                String ownerName = chartNode.get("title").asText();
                String name = series.get("name").asText();
                JsonNode dataArray = series.get("data");
                logger.info("Processing series for ownerName: " + ownerName + " with data points count: " + dataArray.size());

                double[] dataValues = convertJsonArrayToDoubleArray(dataArray);
                String reportName = ownerName + "," + name;
                String reportText = analyzeMetric(reportName, dataValues, startTime, endTime);
                logger.info("Generated report for metric " + metricName + " for owner " + ownerName);

                MetricResponse metricResponse = new MetricResponse();
                metricResponse.setOwnerName(reportName);
//                metricResponse.setMetricValue(convertJsonArrayToList(dataArray));
                metricResponse.setReport(new StringBuilder(reportText));
                metricResponses.add(metricResponse);
                logger.info("Added MetricResponse for owner: " + ownerName);
            }
        } else {
            logger.warn("No series data found in chart for metric: " + metricName);
        }
    }

    private String analyzeMetric(String metricName, double[] data, long startTime, long endTime) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (double value : data) {
            stats.addValue(value);
        }

        double mean = stats.getMean();
        double max = stats.getMax();
        double min = stats.getMin();
        double stdDev = stats.getStandardDeviation();
        double range = max - min;

        String startTimeFormatted = formatTimestamp(startTime);
        String endTimeFormatted = formatTimestamp(endTime);

        StringBuilder report = new StringBuilder();
        report.append("指标名称：").append(metricName).append("\n");
//        report.append("时间区间：").append(startTimeFormatted).append(" 至 ").append(endTimeFormatted).append("\n");
//        report.append("在这段时间区间内的分析结果如下：\n");
        report.append("平均值：").append(String.format("%.5f", mean)).append("\n");
        report.append("最大值：").append(String.format("%.5f", max)).append("\n");
        report.append("最小值：").append(String.format("%.5f", min)).append("\n");
        report.append("波动幅度（最大-最小）：").append(String.format("%.5f", range)).append("\n");
        report.append("标准差：").append(String.format("%.5f", stdDev)).append("\n");

        if (stdDev / mean > 0.1) {
            report.append("波动分析：该指标波动较大，在此时间区间内存在显著的变化。\n");
        } else {
            report.append("波动分析：该指标较为稳定，在此时间区间内变化不大。\n");
        }

        if (max > mean * 1.5) {
            report.append("峰值分析：该指标存在较高峰值，最大值显著高于平均值。\n");
        }

        return report.toString();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS毫秒");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.format(new Date(timestamp));
    }

    private List<Float> convertJsonArrayToList(JsonNode dataArray) {
        List<Float> floatList = new ArrayList<>();
        for (JsonNode valueNode : dataArray) {
            floatList.add(valueNode.isNull() ? 0.0f : valueNode.floatValue());
        }
        return floatList;
    }

    private double[] convertJsonArrayToDoubleArray(JsonNode dataArray) {
        List<Double> dataList = new ArrayList<>();
        for (JsonNode valueNode : dataArray) {
            dataList.add(valueNode.isNull() ? 0.0 : valueNode.asDouble());
        }
        return dataList.stream().mapToDouble(Double::doubleValue).toArray();
    }

    // 获取指定时间段内的服务日志
    @GetMapping("/getLogsByService")
    @OperationLog("获取服务日志")
    public CustomResult getLogsByService(@RequestParam("serviceName") String serviceName,
                                         @RequestParam("namespace") String namespace,
                                         @RequestParam("startTime") long startTime,
                                         @RequestParam("endTime") long endTime) {


        String[] serverNameSplit = serviceName.split("-");
        for (int i = 0; i < serverNameSplit.length; i++) {
            String reducedServiceName = String.join("-", Arrays.copyOf(serverNameSplit, serverNameSplit.length - i));

            // 拼接远程服务 URL
            String url = corootUrl  +  projectId+"/app/" +
                    namespace + ":Deployment:" + reducedServiceName + "/logs?from=" + startTime + "&to=" + endTime;

            logger.info("Sending request to URL: " + url);

            try {
                // 发送请求并获取响应
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                // 检查响应状态
                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("Successfully retrieved logs from remote service");
                    return parseLogs(response.getBody());
                } else {
                    logger.error("Failed to retrieve logs, status code: " + response.getStatusCode());
                    return CustomResult.fail("Failed to retrieve logs");
                }
            } catch (Exception e) {

            }
        }
//        logger.error("Error while retrieving logs: " + null, e);
        return CustomResult.fail("Error retrieving logs");
    }

    private CustomResult parseLogs(String responseBody) {
        List<Object> logEntries = new ArrayList<>();

        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode entries = root.at("/data/entries");

            if (entries.isArray()) {
                logger.info("Found " + entries.size() + " log entries in the specified time range");

                for (JsonNode entry : entries) {
                    logger.debug("Log entry: " + entry);

                    logEntries.add(entry);
                }
            } else {
                logger.warn("No log entries found in the specified time range");
            }

        } catch (Exception e) {
            logger.error("Error parsing logs: " + null, e);
            return CustomResult.fail("Error parsing logs");
        }

        logger.info("Returning " + logEntries.size() + " log entries");
        return CustomResult.ok(logEntries);
    }

    @GetMapping("/getDependencyMap")
    @OperationLog("获取依赖关系图")
    public CustomResult getDependencyMap(@RequestParam("serviceName") String serviceName,
                                         @RequestParam("namespace") String namespace,
                                         @RequestParam("startTime") long startTime,
                                         @RequestParam("endTime") long endTime) {
        // 构建请求 URL
        String url = corootUrl  +  projectId+ "/app/" +
                namespace + ":Deployment:" + serviceName +
                "?from=" + startTime + "&to=" + endTime;

        logger.info("Sending request to URL: " + url);

        try {
            // 发送 GET 请求并获取响应
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // 检查响应状态码
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully retrieved dependency map data");
                return parseDependencyMap(response.getBody());
            } else {
                logger.error("Failed to retrieve dependency map, status code: " + response.getStatusCode());
                return CustomResult.fail("Failed to retrieve dependency map");
            }
        } catch (Exception e) {
            logger.error("Error while retrieving dependency map: " + null, e);
            return CustomResult.fail("Error retrieving dependency map");
        }
    }

    private CustomResult parseDependencyMap(String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode reports = root.at("/data/reports");

            for (JsonNode report : reports) {
                // 定位到 Net 相关报告
                String reportName = report.get("name").asText();
                if ("Net".equalsIgnoreCase(reportName)) {
                    JsonNode widgets = report.get("widgets");
                    for (JsonNode widget : widgets) {
                        // 检查是否包含 dependency_map
                        if (widget.has("dependency_map")) {
                            JsonNode dependencyMap = widget.get("dependency_map");
                            logger.info("Dependency map found for service: " + reportName);
                            return CustomResult.ok(dependencyMap);
                        }
                    }
                }
            }
            logger.warn("No dependency map found for the specified service and time range");
            return CustomResult.fail("No dependency map found");
        } catch (Exception e) {
            logger.error("Error parsing dependency map: " + null, e);
            return CustomResult.fail("Error parsing dependency map");
        }
    }


    // Controller
    @PostMapping("/getTrace")
    @OperationLog("获取追踪信息")
    public CustomResult getTrace(@RequestBody TraceRequestDTO request) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 1. 首先将 query 对象转换为 JSON 字符串
            String queryJson;
            if (request.getQuery() instanceof String) {
                queryJson = (String) request.getQuery();
            } else {
                queryJson = mapper.writeValueAsString(request.getQuery());
            }

            // 2. 对 JSON 字符串进行 URL 编码
            String encodedQuery = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());

            // 3. 构建完整的请求 URL
            String baseUrl = request.getUrl();
            String fullUrl = baseUrl + "?query=" + encodedQuery
                    + "&from=" + request.getFrom()
                    + "&to=" + request.getTo();

            // 打印请求 URL（可用于调试）
            System.out.println("Request URL: " + fullUrl);

            // 创建 RestTemplate 实例
            RestTemplate restTemplate = new RestTemplate();

            // 设置请求头，确保带上 Cookie 信息
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", cookie);  // 确保此 cookie 是有效的
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            // 发送 GET 请求
            ResponseEntity<String> response = restTemplate.exchange(
                    new URI(fullUrl),
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            // 返回响应的主体部分
            if (response.getStatusCode() == HttpStatus.OK) {
                return CustomResult.ok(response.getBody());
            } else {
                log.error("Failed to get trace data, status: {}", response.getStatusCode());
                return CustomResult.fail("Failed to get trace data");
            }

        } catch (Exception e) {
            log.error("Error getting trace data", e);
            return CustomResult.fail(null);
        }
    }
    @GetMapping("/testTrace")
    @OperationLog("测试追踪")
    public Object testTrace() throws UnsupportedEncodingException, URISyntaxException {
        // 构建 query 参数，确保它是有效的 JSON 字符串，并对其进行 URL 编码
        String queryParam = "{\"view\":\"traces\"}";
        String baseUrl = "http://60.245.215.207:8083/api/project/ld4lglgc/overview/traces";

        String encodedQueryParam = URLEncoder.encode(queryParam, String.valueOf(StandardCharsets.UTF_8));
        String requestUrl = baseUrl + "?query=" + encodedQueryParam + "&from=1738739080958&to=1738740360000" ;
        // 使用 UriComponentsBuilder 来构建 URL
        System.out.println(requestUrl);
        // 创建 RestTemplate 实例
        // 设置请求头，确保带上 Cookie 信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);  // 确保此 cookie 是有效的
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                new URI(requestUrl),
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // 返回响应的主体部分
        return response.getBody();
    }
}
package com.iscas.lndicatormonitor.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@Component
public class PrometheusUtil {
    @Value("${prometheus.url}")
    private String prometheusUrl;

    private static final NavigableMap<Long, Integer> TIME_STEP_MAP = new TreeMap<>();


    static {
        TIME_STEP_MAP.put(1000L, 1);        // 1s
        TIME_STEP_MAP.put(10000L, 1);       // 10s
        TIME_STEP_MAP.put(60000L, 1);       // 1min
        TIME_STEP_MAP.put(300000L, 1);      // 5min
        TIME_STEP_MAP.put(900000L, 3);      // 15min
        TIME_STEP_MAP.put(1800000L, 7);     // 30min
        TIME_STEP_MAP.put(3600000L, 14);    // 1h
        TIME_STEP_MAP.put(7200000L, 28);    // 2h
        TIME_STEP_MAP.put(21600000L, 86);   // 6h
        TIME_STEP_MAP.put(43200000L, 172);  // 12h
        TIME_STEP_MAP.put(86400000L, 345);  // 1d
        TIME_STEP_MAP.put(172800000L, 691); // 2d
    }
    /**
     * 请求 Prometheus，获取指标数据
     *
     * @param query Prometheus 查询表达式
     * @param start 起始时间（Unix 时间戳，单位为秒，可以是小数）
     * @param end   结束时间（Unix 时间戳，单位为秒）
     * @param step  步长，单位为秒
     * @return Prometheus 响应数据（JSON 格式字符串），如果请求失败则返回 null
     */
    public String queryRange(String query, double start, double end, int step) {
        try {
            // 格式化时间戳为小数格式
            String startFormatted = String.format("%.3f", start);
            String endFormatted = String.format("%.3f", end);

            // 对 query 参数进行 URL 编码
//            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

            // 拼接完整 URL
            String requestUrl = prometheusUrl + "?query=" + query + "&start=" + startFormatted + "&end=" + endFormatted + "&step=" + step;


            return requestUrl;

        } catch (Exception e) {
            // 捕获异常，记录错误日志
            System.err.println("请求 Prometheus 时发生异常：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String calculateDuration(Date startTime, Date endTime) {
        if (endTime == null) {
            endTime = new Date(); // 如果 endTime 为 null，使用当前时间
        }
        long durationInSeconds = (endTime.getTime() - startTime.getTime()) / 1000;
        long hours = durationInSeconds / 3600;
        long minutes = (durationInSeconds % 3600) / 60;
        long seconds = durationInSeconds % 60;
        return hours + "h " + minutes + "min " + seconds + "s";
    }

    public Integer calculateStep(Date startTime, Date endTime) {
        long diff = endTime.getTime() - startTime.getTime();
        if (diff <= 0) {
            throw new IllegalArgumentException("EndTime must be after startTime");
        }
        Map.Entry<Long, Integer> entry = TIME_STEP_MAP.floorEntry(diff);
        if (entry != null) {
            return entry.getValue();
        }
        return 2419; // For duration > 2d
    }

    /**
     * 拼接 Prometheus 查询的标签选择器
     *
     * @param queryBuilder 查询字符串构建器
     * @param params       标签键值对
     */
    public static void appendLabelSelectors(StringBuilder queryBuilder, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            queryBuilder.append("{");
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (i > 0) {
                    queryBuilder.append(",");
                }
                queryBuilder.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                i++;
            }
            queryBuilder.append("}");
        }
    }
}

//package com.iscas.lndicatormonitor;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.iscas.lndicatormonitor.dto.coroot.TraceRequestFilter;
//import com.iscas.lndicatormonitor.dto.coroot.TraceRequestQuery;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class JsonEqualityTest {
//
//    private ObjectMapper mapper;
//    private TraceRequestQuery traceRequestQuery;
//
//    private final String queryJson1 = "{\"view\":\"traces\",\"filters\":[{\"field\":\"ServiceName\",\"op\":\"=\",\"value\":\"ts-travel2-service\"},{\"field\":\"SpanName\",\"op\":\"=\",\"value\":\"POST /api/v1/travel2service/trips/left\"}]}";
//
//    @BeforeEach
//    public void setup() {
//        // 初始化 ObjectMapper 并配置以尝试匹配手动 JSON
//        mapper = new ObjectMapper();
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);  // 忽略 null 值
//        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true); // 排序键
//        mapper.configure(SerializationFeature.INDENT_OUTPUT, false); // 禁用缩进
//
//        // 构建 traceRequestQuery 对象
//        traceRequestQuery = new TraceRequestQuery();
//        traceRequestQuery.setView("traces");
//
//        TraceRequestFilter filter1 = new TraceRequestFilter();
//        filter1.setField("ServiceName");
//        filter1.setOp("=");
//        filter1.setValue("ts-travel2-service");
//
//        TraceRequestFilter filter2 = new TraceRequestFilter();
//        filter2.setField("SpanName");
//        filter2.setOp("=");
//        filter2.setValue("POST /api/v1/travel2service/trips/left");
//
//        traceRequestQuery.setFilters(Arrays.asList(filter1, filter2));
//    }
//
//    @Test
//    public void testJsonEquality() throws Exception {
//        // 使用 ObjectMapper 将 traceRequestQuery 转换为 JSON，并移除多余空白字符
//        String queryJson2 = mapper.writeValueAsString(traceRequestQuery);
//
//        System.out.println("Manual JSON    : " + queryJson1);
//        System.out.println("Generated JSON : " + queryJson2);
//        System.out.println("isEqual: " + queryJson1.equals(queryJson2));
//        // 比较 queryJson1 和 queryJson2 是否相等
//        assertEquals(queryJson1, queryJson2, "Generated JSON does not match the manual JSON string.");
//    }
//}

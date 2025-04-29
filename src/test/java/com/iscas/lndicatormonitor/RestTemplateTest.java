//package com.iscas.lndicatormonitor;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
//@SpringBootTest
//public class RestTemplateTest {
//
//    @Autowired
//    private  RestTemplate restTemplate;
//    private final String baseUrl = "http://60.245.215.207:8083/api/project/pqnv7ole/overview/traces";
//    private final String queryJson = "{\"view\":\"traces\",\"filters\":[{\"field\":\"ServiceName\",\"op\":\"=\",\"value\":\"ts-travel2-service\"},{\"field\":\"SpanName\",\"op\":\"=\",\"value\":\"POST /api/v1/travel2service/trips/left\"}]}";
//
//    @Test
//    public void testWithoutEncoding() {
//        try {
//            String requestUrl = baseUrl + "?query=" + queryJson + "&from=now-12h";
//            System.out.println("Testing without encoding:\n" + requestUrl);
//            ResponseEntity<String> response = restTemplate.getForEntity(new URI(requestUrl), String.class);
//            System.out.println(response.getBody());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testSingleEncoding() {
//        try {
//            String encodedQuery = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());
//            String requestUrl = baseUrl + "?query=" + encodedQuery + "&from=now-12h";
//            System.out.println("Testing with single encoding:\n" + requestUrl);
//            ResponseEntity<String> response = restTemplate.getForEntity(new URI(requestUrl), String.class);
//            System.out.println(response.getBody());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testDoubleEncoding() {
//        try {
//            String firstEncode = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());
//            String doubleEncodedQuery = URLEncoder.encode(firstEncode, StandardCharsets.UTF_8.toString());
//            String requestUrl = baseUrl + "?query=" + doubleEncodedQuery + "&from=now-12h";
//            System.out.println("Testing with double encoding:\n" + requestUrl);
//            ResponseEntity<String> response = restTemplate.getForEntity(new URI(requestUrl), String.class);
//            System.out.println(response.getBody());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testUsingUriComponentsBuilderSingleEncoding() {
//        try {
//            String encodedQuery = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());
//            URI uri = UriComponentsBuilder
//                    .fromHttpUrl(baseUrl)
//                    .queryParam("query", encodedQuery)
//                    .queryParam("from", "now-12h")
//                    .build(true) // 保持已编码的内容
//                    .toUri();
//            System.out.println("Testing with UriComponentsBuilder (single encoding):\n" + uri);
//            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
//            System.out.println(response.getBody());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testUsingUriComponentsBuilderDoubleEncoding() {
//        try {
//            String firstEncode = URLEncoder.encode(queryJson, StandardCharsets.UTF_8.toString());
//            String doubleEncodedQuery = URLEncoder.encode(firstEncode, StandardCharsets.UTF_8.toString());
//            URI uri = UriComponentsBuilder
//                    .fromHttpUrl(baseUrl)
//                    .queryParam("query", doubleEncodedQuery)
//                    .queryParam("from", "now-12h")
//                    .build(true) // 保持已编码的内容
//                    .toUri();
//            System.out.println("Testing with UriComponentsBuilder (double encoding):\n" + uri);
//            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
//            System.out.println(response.getBody());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

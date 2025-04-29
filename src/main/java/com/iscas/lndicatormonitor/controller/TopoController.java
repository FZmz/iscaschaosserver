package com.iscas.lndicatormonitor.controller;

import com.google.gson.Gson;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Linkrequest;
import com.iscas.lndicatormonitor.domain.request.Request;
import com.iscas.lndicatormonitor.domain.request.SystemTopoRequest;
import com.iscas.lndicatormonitor.domain.topo.Edge;
import com.iscas.lndicatormonitor.domain.topo.Node;
import com.iscas.lndicatormonitor.domain.topo.Topo;
import com.iscas.lndicatormonitor.dto.traceRequest.TraceRequest;
import com.iscas.lndicatormonitor.service.LinkrequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/topo")
public class TopoController {
    @Autowired
    private LinkrequestService linkrequestService;
    @Autowired
    private RestTemplate restTemplate;
    // 根据被测应用名获取系统拓扑图
    @RequestMapping("/getTopoByAppName")
    public CustomResult getTopoByAppName(String appName){
        Topo topo = new Topo();
        topo.addNode(new Node("frontend^node", "frontend", "node"));
        topo.addNode(new Node("user^node", "user", "node"));
        topo.addNode(new Node("user-db^mongo", "user-db", "mongo"));
        topo.addNode(new Node("order^node", "order", "node"));
        topo.addNode(new Node("order-db^mysql", "order-db", "mysql"));
        topo.addNode(new Node("carts^node", "carts", "node"));
        topo.addNode(new Node("carts-db^mongo", "carts-db", "mongo"));
        topo.addNode(new Node("payment^node", "payment", "node"));
        topo.addNode(new Node("queue^node", "queue", "node"));
        topo.addNode(new Node("shipping^node", "shipping", "node"));
        topo.addNode(new Node("cataloue^node", "cataloue", "node"));  // Note: Corrected the typo from "cataloue" to "catalogue"
        topo.addNode(new Node("cataloue-db^redis", "cataloue-db", "redis"));
        topo.addNode(new Node("Rabbitmq^rabbitmq", "Rabbitmq", "Rabbitmq"));
        topo.addEdge(new Edge("frontend^node", "user^node"));
        topo.addEdge(new Edge("frontend^node", "order^node"));
        topo.addEdge(new Edge("frontend^node", "cataloue^node"));
        topo.addEdge(new Edge("user^node", "user-db^mongo"));
        topo.addEdge(new Edge("order^node", "order-db^mysql"));
        topo.addEdge(new Edge("order^node", "carts^node"));
        topo.addEdge(new Edge("order^node", "payment^node"));
        topo.addEdge(new Edge("order^node", "queue^node"));
        topo.addEdge(new Edge("order^node", "shipping^node"));
        topo.addEdge(new Edge("carts^node", "carts-db^mongo"));
        topo.addEdge(new Edge("payment^node", "Rabbitmq^rabbitmq"));
        topo.addEdge(new Edge("queue^node", "Rabbitmq^rabbitmq"));
        topo.addEdge(new Edge("shipping^node", "Rabbitmq^rabbitmq"));
        topo.addEdge(new Edge("cataloue^node", "cataloue-db^redis"));  // Note: Corrected the typo from "cataloue" to "catalogue"
        return new CustomResult(20000,"获取成功",topo);
    }

    // 新增一条linkrequest记录
    @PostMapping("/addQuest")
    @OperationLog("新增一条linkrequest记录")
    public CustomResult addLinkrequest(@RequestBody Linkrequest linkrequest) {
        linkrequestService.insert(linkrequest);
        // 发起请求至被测应用
        String req = linkrequest.getContent();
        // 创建Gson实例
        Gson gson = new Gson();
        // 将JSON字符串转换为TraceRequest对象
        TraceRequest traceRequest = gson.fromJson(req, TraceRequest.class);
        Object response = null;
        // 搭建对traceRequest.getURL()的请求
        // 1、处理url
        String url = traceRequest.getUrl();
        // 2、处理method
        String method = traceRequest.getMethod();
        // 3、处理header
        HttpHeaders headers = new HttpHeaders();
        // 添加Content-Type
        headers.setContentType(MediaType.APPLICATION_JSON);
        for (String key : traceRequest.getHeader().keySet()) {
            Object value = traceRequest.getHeader().get(key);

            // 检查值是否为 Double 类型
            if (value instanceof Double) {
                // 将 Double 类型的值转换为整数
                int intValue = ((Double) value).intValue();
                // 将整数值转换为字符串并添加到 headers 中
                headers.add(key, String.valueOf(intValue));
            } else {
                // 对于非 Double 类型，直接转换为字符串
                headers.add(key, String.valueOf(value));
            }
        }
        // 4、处理body
        // 4.1 处理data
        if (traceRequest.getData() != null) {
            // 发起请求
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(traceRequest.getData(), headers);
            response = restTemplate.postForObject(url, requestEntity, Object.class);
        }else {
            // 4.2 处理params
            url += "?";
            for (String key : traceRequest.getParams().keySet()) {
                url += key + "=" + traceRequest.getParams().get(key) + "&";
            }
            // 去掉最后一个&
            url = url.substring(0, url.length() - 1);
            // 发起请求
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);
            response = restTemplate.postForObject(url, requestEntity, Object.class);
        }
        return new CustomResult(20000, "success",response);
    }
    // 根据请求获取链路调用拓扑图
    @PostMapping("/getTopoByRequest")
    @OperationLog("根据请求获取链路调用拓扑图")
    public CustomResult getTopoByRequest(@RequestBody Request httpRequest){
        Topo topo = new Topo();
/*        topo.addNode(new Node("front-end", "front-end", "SPRING_BOOT"));
        topo.addNode(new Node("session-db", "session-db", "mysql"));
        topo.addNode(new Node("catalogue", "catalogue", "SPRING_BOOT"));
        topo.addNode(new Node("catalogue-db", "catalogue-db", "mysql"));
        topo.addEdge(new Edge("front-end", "session-db"));
        topo.addEdge(new Edge("front-end", "catalogue"));
        topo.addEdge(new Edge("catalogue", "catalogue-db"));*/

        // ts-contacts.yaml                ts-order.yaml                   ts-security.yaml                ts-travel-route-1.0.yaml
        //ts-food.yaml                    ts-preserve.yaml                ts-station.yml                  ts-user.yml
        //ts-frontend.yaml                ts-seat.yaml                    ts-ticketinfo.yaml


        topo.addNode(new Node("ts-contacts", "ts-contacts", "SPRING_BOOT"));
        topo.addNode(new Node("ts-contacts-mongo", "ts-contacts-mongo", "SPRING_BOOT"));

        topo.addNode(new Node("ts-order", "ts-order", "SPRING_BOOT"));
        topo.addNode(new Node("ts-order-mongo", "ts-order-mongo", "SPRING_BOOT"));

        topo.addNode(new Node("ts-order-other", "ts-order-other", "SPRING_BOOT"));
        topo.addNode(new Node("ts-order-other-mongo", "ts-order-other-mongo", "SPRING_BOOT"));

        topo.addNode(new Node("ts-security", "ts-security", "SPRING_BOOT"));
        topo.addNode(new Node("ts-security-mongo", "ts-security-mongo", "SPRING_BOOT"));

        topo.addNode(new Node("ts-travel", "ts-travel", "SPRING_BOOT"));
        topo.addNode(new Node("ts-travel-mongo", "ts-travel-mongo", "SPRING_BOOT"));


        topo.addNode(new Node("ts-food", "ts-food", "SPRING_BOOT"));
        topo.addNode(new Node("ts-food-mongo", "ts-food-mongo", "SPRING_BOOT"));

        topo.addNode(new Node("ts-preserve", "ts-preserve", "SPRING_BOOT"));
        topo.addNode(new Node("ts-voucher-mysql", "ts-voucher-mysql", "SPRING_BOOT"));

        topo.addNode(new Node("ts-station", "ts-station", "SPRING_BOOT"));
        topo.addNode(new Node("ts-station-mongo", "ts-station-mongo", "SPRING_BOOT"));



        topo.addNode(new Node("ts-user", "ts-user", "SPRING_BOOT"));
        topo.addNode(new Node("ts-user-mongo", "ts-user-mongo", "SPRING_BOOT"));

        topo.addNode(new Node("ts-seat", "ts-seat", "SPRING_BOOT"));
        topo.addNode(new Node("ts-ticketinfo", "ts-ticketinfo", "SPRING_BOOT"));


        topo.addEdge(new Edge("ts-preserve", "ts-contacts"));
        topo.addEdge(new Edge("ts-contacts", "ts-contacts-mongo"));

        topo.addEdge(new Edge("ts-preserve", "ts-food"));
        topo.addEdge(new Edge("ts-food", "ts-food-mongo"));

        topo.addEdge(new Edge("ts-preserve", "ts-seat"));

        topo.addEdge(new Edge("ts-preserve", "ts-ticketinfo"));

        topo.addEdge(new Edge("ts-preserve", "ts-travel"));
        topo.addEdge(new Edge("ts-travel", "ts-travel-mongo"));

        topo.addEdge(new Edge("ts-preserve", "ts-voucher-mysql"));


        topo.addEdge(new Edge("ts-preserve", "ts-user"));
        topo.addEdge(new Edge("ts-user", "ts-user-mongo"));

        topo.addEdge(new Edge("ts-preserve", "ts-order"));
        topo.addEdge(new Edge("ts-order", "ts-order-mongo"));

        topo.addEdge(new Edge("ts-preserve", "ts-order-other"));
        topo.addEdge(new Edge("ts-order-other", "ts-order-other-mongo"));

        topo.addEdge(new Edge("ts-preserve", "ts-security"));
        topo.addEdge(new Edge("ts-security", "ts-security-mongo"));

        topo.addEdge(new Edge("ts-preserve", "ts-station"));
        topo.addEdge(new Edge("ts-station", "ts-station-mongo"));



        return new CustomResult(20000,"获取成功",topo);
    }

    @GetMapping("/getApplicationName")
    @OperationLog("获取应用程序名称")
    public CustomResult getApplicationName(){
        // post 请求http://39.104.62.233:8051/map/getApplicationName
        // 定义远程服务的 URL
        String url = "http://39.104.62.233:8347/map/getApplicationName";
        System.out.println(url);
        // 创建 HttpHeaders 实例，并设置 Content-Type 为 application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 如果远程服务不需要请求体，可以发送一个空的 HttpEntity
        HttpEntity<String> requestEntity = new HttpEntity<>("{}", headers);

        try {
            // 使用 RestTemplate 发起 POST 请求并接收响应
            Object applicationNames = restTemplate.postForObject(url, requestEntity, Object.class);
            System.out.println(applicationNames);
            // 检查返回的应用程序名称是否为空
            if (applicationNames != null) {
                return new CustomResult(20000, "获取成功", applicationNames);
            } else {
                return new CustomResult(20001, "获取应用程序名称失败，响应为空", null);
            }
        } catch (Exception e) {
            // 处理请求失败的情况
            return new CustomResult(20002, "获取应用程序名称失败：" + null, null);
        }
    }
    @PostMapping("/getSystemTopo")
    public CustomResult getServerMap(@RequestBody SystemTopoRequest systemTopoRequest) {
        String url = "http://39.104.62.233:8347/map/getServerMap";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        // 假设 SystemTopoRequest 有字段 fieldName1, fieldName2
        // 将每个字段作为一个表单参数
        map.add("applicationName", systemTopoRequest.getApplicationName());
        map.add("serviceTypeName", systemTopoRequest.getServiceTypeName());
        map.add("duration", systemTopoRequest.getDuration());
        // 添加更多字段...
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        try {
            Object systemTopo = restTemplate.postForObject(url, requestEntity, Object.class);

            if (systemTopo != null) {
                return new CustomResult(20000, "获取成功", systemTopo);
            } else {
                return new CustomResult(40000, "获取系统拓扑图失败，响应为空", null);
            }
        } catch (Exception e) {
            return new CustomResult(40000, "获取系统拓扑图失败：" + null, null);
        }
    }
    @GetMapping("/getLinkTopo")
    @OperationLog("获取链路拓扑图")
    public CustomResult getLinkTopo(@RequestParam String taskId) {
        // post 请求http://39.104.62.233:8350/map/getTraceMap
        // 定义远程服务的 URL
        String url = "http://39.104.62.233:8347/map/getTraceMap";
        System.out.println(url);
        // 创建 HttpHeaders 实例，并设置 Content-Type 为 application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 请求体有一个字段 taskId
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("taskId", taskId);
        // 发起请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);
        try {
            // 使用 RestTemplate 发起 POST 请求并接收响应
            Object linkTopo = restTemplate.postForObject(url, requestEntity, Object.class);
            // 检查返回的链路拓扑图是否为空
            if (linkTopo != null) {
                return new CustomResult(20000, "获取成功", linkTopo);
            } else {
                return new CustomResult(40000, "获取链路拓扑图失败，响应为空", null);
            }
        } catch (Exception e) {
            // 处理请求失败的情况
            return new CustomResult(40000, "获取链路拓扑图失败：" + null, null);
        }
    }


}

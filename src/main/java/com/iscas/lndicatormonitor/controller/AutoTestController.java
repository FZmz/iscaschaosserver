package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/autoTest")
public class AutoTestController {
    @Value("${autoTest.url}")
    private String autoTestUrl;

    @Autowired
    RestTemplate restTemplate;

    /*
    * 获取追踪数据
    *
    * */
    @PostMapping("/getTraceData")
    @OperationLog("获取追踪数据")
    public CustomResult getTaskListByPage(@RequestBody Object selectReq) {
        String perfix = "/getTraceData";
        String method = "POST";
        CustomResult result = new CustomResult();
        String url = autoTestUrl + perfix;
        try {
            // 发起 HTTP POST 请求
            Object response = restTemplate.postForObject(url, selectReq, Object.class);
            // 将响应结果封装到 CustomResult 对象中
            result.setData(response);
            result.setStatus(20000);
            result.setMsg("获取追踪树成功");
        } catch (Exception e) {
            result.setStatus(40000);
            result.setMsg("获取追踪树数据失败: " + null);
        }

        return result;
    }

    /*
    * 新增任务
    * */
    @PostMapping("/addTask")
    public CustomResult addTask(@RequestBody Object task){
        String perfix = "/addTask";
        String method = "POST";
        CustomResult result = new CustomResult();
        String url = autoTestUrl + perfix;
        try {
            // 发起 HTTP POST 请求
            Object response = restTemplate.postForObject(url, task, Object.class);
            // 将响应结果封装到 CustomResult 对象中
            result.setData(response);
            result.setStatus(20000);
            result.setMsg("新增任务成功");
        } catch (Exception e) {
            result.setStatus(40000);
            result.setMsg("新增任务失败: " + null);
        }

        return result;
    }

    /*
    * 上传apiDoc
    * */
    @PostMapping("/addApiDoc")
    @OperationLog("上传API文档")
    public CustomResult addApiDoc(@RequestBody Object apiDoc) throws IOException {
        // 读取上传的JSON文件内容
        String perfix = "/addApiDoc";
        String method = "POST";
        CustomResult result = new CustomResult();
        String url = autoTestUrl + perfix;
        try {
            // 发起 HTTP POST 请求
            Object response = restTemplate.postForObject(url, apiDoc, Object.class);
            // 将响应结果封装到 CustomResult 对象中
            result.setData(response);
            result.setStatus(20000);
            result.setMsg("新增ApiDoc成功");
        } catch (Exception e) {
            result.setStatus(40000);
            result.setMsg("新增ApiDoc失败: " + null);
        }
        return result;
    }

    /*
    * 分页获取测试任务下的测试记录
    * */
    @GetMapping("/getRecordByTaskId")
    @OperationLog("获取测试记录")
    public CustomResult getRecordByTaskId(@RequestParam String taskId, @RequestParam int page, @RequestParam int page_size) {
        String perfix = "/getRecordByTaskId";
        String method = "GET";
        CustomResult result = new CustomResult();
        String url = autoTestUrl + perfix + "?task_id=" + taskId + "&page=" + page + "&page_size=" + page_size;
        try {
            // 发起 HTTP GET 请求
            Object response = restTemplate.getForObject(url, Object.class);
            // 将响应结果封装到 CustomResult 对象中
            result.setData(response);
            result.setStatus(20000);
            result.setMsg("获取测试记录成功");
        } catch (Exception e) {
            result.setStatus(40000);
            result.setMsg("获取测试记录失败: " + null);
        }
        return result;
    }
    /*
     * 分页获取测试任务
     * */
    @GetMapping("/getTaskByPage")
    @OperationLog("分页获取测试任务")
    public CustomResult getTaskByPage(@RequestParam int page, @RequestParam int page_size) {
        String perfix = "/getTaskByPage";
        String method = "GET";
        CustomResult result = new CustomResult();
        String url = autoTestUrl + perfix + "?page=" + page + "&page_size=" + page_size;
        try {
            // 发起 HTTP GET 请求
            Object response = restTemplate.getForObject(url, Object.class);
            // 将响应结果封装到 CustomResult 对象中
            result.setData(response);
            result.setStatus(20000);
            result.setMsg("获取测试任务成功");
        } catch (Exception e){
            result.setStatus(40000);
            result.setMsg("获取测试任务失败: " + null);
        }
        return result;
    }


    /*
     * 执行测试任务
     * */
    @PostMapping("/execTask")
    @OperationLog("执行测试任务")
    public CustomResult execTask(@RequestParam String taskId) {
        String perfix = "/execTask";
        String method = "POST";
        CustomResult result = new CustomResult();
        String url = autoTestUrl + perfix + "?task_id=" + taskId;
        try {
            // 发起 HTTP POST 请求
            Object response = restTemplate.postForObject(url, null, Object.class);
            // 将响应结果封装到 CustomResult 对象中
            result.setData(response);
            result.setStatus(20000);
            result.setMsg("执行任务成功");
        } catch (Exception e) {
            result.setStatus(40000);
            result.setMsg("执行任务失败: " + null);
        }
        return result;
    }

    /*
    *  根据recordId获取Trace
    * */
    @GetMapping("/getTraceByRecordId")
    @OperationLog("获取Trace记录")
    public CustomResult getTraceByRecordId(@RequestParam String recordId){
        String perfix = "/getTraceByRecordId";
        String method = "GET";
        CustomResult result = new CustomResult();
        String url = autoTestUrl + perfix + "?record_id=" + recordId;
        try {
            // 发起 HTTP GET 请求
            Object response = restTemplate.getForObject(url, Object.class);
            // 将响应结果封装到 CustomResult 对象中
            result.setData(response);
            result.setStatus(20000);
            result.setMsg("获取Trace成功");
        } catch (Exception e){
            result.setStatus(40000);
            result.setMsg("获取Trace失败: " + null);
        }
        return result;
    }
    /*
    * 根据recordId获取测试微服务状态情况
    * */
    @GetMapping("/getMSStatusByRecordId")
    @OperationLog("获取微服务状态")
    public CustomResult getMSStatusByRecordId(@RequestParam String recordId){
        String perfix = "/getMSStatusByRecordId";
        String method = "GET";
        CustomResult result = new CustomResult();
        String url = autoTestUrl + perfix + "?record_id=" + recordId;
        try {
            // 发起 HTTP GET 请求
            Object response = restTemplate.getForObject(url, Object.class);
            // 将响应结果封装到 CustomResult 对象中
            result.setData(response);
            result.setStatus(20000);
            result.setMsg("获取微服务状态成功");
        } catch (Exception e){
            result.setStatus(40000);
            result.setMsg("获取微服务状态失败: " + null);
        }
        return result;
    }

    /*
    * 根据微服务Id获取测试用例执行详情
    * */
    @GetMapping("/getCaseDetailByMSId")
    @OperationLog("获取测试用例执行详情")
    public CustomResult getCaseDetailByMSId(@RequestParam String msId,@RequestParam int page, @RequestParam int page_size){
        String perfix = "/getMSRunDetailByMSId";
        String method = "GET";
        CustomResult result = new CustomResult();
        String url = autoTestUrl + perfix + "?ms_id=" + msId + "&page=" + page + "&page_size=" + page_size;
        try {
            // 发起 HTTP GET 请求
            Object response = restTemplate.getForObject(url, Object.class);
            // 将响应结果封装到 CustomResult 对象中
            result.setData(response);
            result.setStatus(20000);
            result.setMsg("获取测试用例执行详情成功");
        }catch (Exception e){
            result.setStatus(40000);
            result.setMsg("获取测试用例执行详情失败: " + null);
        }
        return result;
    }

    
}

package com.iscas.lndicatormonitor.controller;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.dto.OverAllItem;
import com.iscas.lndicatormonitor.dto.request.TestRequest;
import com.iscas.lndicatormonitor.dto.request.TestRequestDetail;
import com.iscas.lndicatormonitor.dto.response.StartTestRsp;
import com.iscas.lndicatormonitor.dto.task.FaultConfigItem;
import com.iscas.lndicatormonitor.dto.task.TaskSpec;
import com.iscas.lndicatormonitor.entity.Task;
import com.iscas.lndicatormonitor.entity.TaskPage;
import com.iscas.lndicatormonitor.repository.TaskRepository;
import com.iscas.lndicatormonitor.utils.JwtTokenUtil;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${chaos.mesh.url}")
    private String chaosMeshUrl;
    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private RestTemplate restTemplate;
    @PostMapping("/add")
    @OperationLog("新增任务")
    public CustomResult addTask(@RequestBody TaskSpec jsonObject) throws Exception{
        List<Document> faultConfigDocuments = new ArrayList<>();
        for (FaultConfigItem faultConfigItem : jsonObject.getFault_config()) {
            // 创建FaultConfigItem的Document表示
            Document faultConfigDocument = new Document("service_name", faultConfigItem.getService_name())
                    .append("file_id", new ObjectId(faultConfigItem.getFile_id()))
                    .append("fault_list", faultConfigItem.getFault_list()); // 确保fault_list中的元素可以直接转换为Document接受的类型
            faultConfigDocuments.add(faultConfigDocument);
        }
    // 假设 jsonObject.getFile_list() 返回一个字符串列表
        List<String> stringList = jsonObject.getFile_list();

    // 将字符串列表转换为 ObjectId 列表
        List<ObjectId> objectIdList = stringList.stream()
                .map(ObjectId::new)  // 将每个字符串转换为 ObjectId
                .collect(Collectors.toList());  // 收集结果到列表

        // 将TaskSpec插入到MongoDB的task集合中
        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("task");
        // 构建插入文档
        Document document = new Document("createTime", jsonObject.getCreateTime())
                .append("creator", jsonObject.getCreator())
                .append("fault_config", faultConfigDocuments)
                .append("file_list", objectIdList)
                .append("linkGraphData", jsonObject.getLinkGraphData()) // 确保linkGraphData可以被转换为Document
                .append("name", jsonObject.getName())
                .append("namespace", jsonObject.getNamespace())
                .append("selected_req", jsonObject.getSelected_req()) // 确保selected_req可以被转换为Document
                .append("systemGraphData", jsonObject.getSystemGraphData()) // 确保systemGraphData可以被转换为Document
                .append("testnamespace", jsonObject.getTestnamespace())
                .append("apiDoc", jsonObject.getApiDoc());

        // 插入文档
        collection.insertOne(document);
        // 获取插入文档的ID
        ObjectId id = (ObjectId) document.get("_id");
        return CustomResult.ok(id);
    }

    // 更新任务
    @PostMapping("/update")
    @OperationLog("更新任务")
    public CustomResult updateTask(@RequestBody TaskSpec jsonObject){
        List<Document> faultConfigDocuments = new ArrayList<>();
        for (FaultConfigItem faultConfigItem : jsonObject.getFault_config()) {
            // 创建FaultConfigItem的Document表示
            Document faultConfigDocument = new Document("service_name", faultConfigItem.getService_name())
                    .append("file_id", new ObjectId(faultConfigItem.getFile_id()))
                    .append("fault_list", faultConfigItem.getFault_list()); // 确保fault_list中的元素可以直接转换为Document接受的类型
            faultConfigDocuments.add(faultConfigDocument);
        }
        // 假设 jsonObject.getFile_list() 返回一个字符串列表
        List<String> stringList = jsonObject.getFile_list();

        // 将字符串列表转换为 ObjectId 列表
        List<ObjectId> objectIdList = stringList.stream()
                .map(ObjectId::new)  // 将每个字符串转换为 ObjectId
                .collect(Collectors.toList());  // 收集结果到列表

        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("task");
        // 构建查询条件
        Document query = new Document("_id", new ObjectId(jsonObject.getId()));
        // 构建更新文档
        Document update = new Document("createTime", jsonObject.getCreateTime())
                .append("creator", jsonObject.getCreator())
                .append("fault_config", faultConfigDocuments)
                .append("file_list", objectIdList)
                .append("linkGraphData", jsonObject.getLinkGraphData()) // 确保linkGraphData可以被转换为Document
                .append("name", jsonObject.getName())
                .append("namespace", jsonObject.getNamespace())
                .append("selected_req", jsonObject.getSelected_req()) // 确保selected_req可以被转换为Document
                .append("systemGraphData", jsonObject.getSystemGraphData()) // 确保systemGraphData可以被转换为Document
                .append("testnamespace", jsonObject.getTestnamespace())
                .append("apiDoc", jsonObject.getApiDoc());
        // 执行更新
        try {
            collection.updateOne(query, new Document("$set", update));
        }catch (Exception e){
            return new  CustomResult(40000,"更新失败",null);
        }
        return CustomResult.ok("更新成功");
    }
    // 删除任务
    @PostMapping("/delete")
    @OperationLog("删除任务")
    public CustomResult deleteTask(@RequestBody Map<String, String> payload){
        String task_id = payload.get("task_id");
        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("task");
        // 构建查询条件
        Document query = new Document("_id", new ObjectId(task_id));
        // 执行删除
        try {
            collection.deleteOne(query);
        }catch (Exception e){
            return new  CustomResult(40000,"删除失败",null);
        }
        return CustomResult.ok("删除成功");
    }
    @GetMapping("/getTaskById")
    @OperationLog("获取任务")
    public CustomResult getTaskById(@RequestParam("task_id") String taskId) throws Exception{
        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("task");

        // 构建查询条件
        Document query = new Document("_id", taskId);

        // 执行查询并获取单个文档
        Document taskDocument = collection.find(query).first();

        return CustomResult.ok(taskDocument);

    }

    @GetMapping("/getAllByPage")
    @OperationLog("获取任务列表")
    public CustomResult getAllTaskByPage(@RequestParam Integer page, Integer pageSize, String sort){

        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("task");
        Long count = collection.countDocuments();

        // 定义分页参数
        int skip = (page - 1) * pageSize;

        // 构建聚合查询
        AggregateIterable<Document> aggregation = collection.aggregate(Arrays.asList(
                Aggregates.skip(skip),
                Aggregates.limit(pageSize)
        ));

        List<String> arraylist = new ArrayList<>();
        // 遍历查询结果
        for (Document result : aggregation) {
            String jsonpObject = result.toJson();
            arraylist.add(jsonpObject);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("currentPage",page);
        map.put("pageSize",pageSize);
        map.put("totalOrders",count);
        map.put("totalPages",count / pageSize + 1);
        map.put("task",arraylist);

        return CustomResult.ok(map);
    }

    @GetMapping("/getDetailByReqId")
    @OperationLog("获取请求详情")
    public CustomResult getDetailByReqId(@RequestParam String req_id,Integer page, Integer pageSize, String sort){
        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("requestDetail");
        // 构建查询条件
        Document query = new Document("_id", new ObjectId(req_id));
        // 执行查询并获取单个文档
        Document requestDocument = collection.find(query).first();
        List<ObjectId> dependIdList = (List<ObjectId>) requestDocument.get("depend_ids");
        List<Document> dependList = new ArrayList<>();
        dependList.add(requestDocument);
        for (ObjectId dependId : dependIdList) {
            Document dependDocument = collection.find(new Document("_id", dependId)).first();
            dependList.add(dependDocument);
        }
        // 对查询结果进行分页处理
        int skip = (page - 1) * pageSize;
        int limit = pageSize;
        List<Document> subList = dependList.subList(skip, Math.min(skip + limit, dependList.size()));
        // 将subList的_id转换为字符串
        for (Document doc : subList) {
            doc.put("_id", doc.getObjectId("_id").toString());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("currentPage", page);
        map.put("pageSize", pageSize);
        map.put("totalOrders", dependList.size());
        map.put("totalPages", (dependList.size() / pageSize) + 1);
        map.put("requestDetail", subList);
        return CustomResult.ok(map);
    }
    @GetMapping("/getReqByRecordId")
    @OperationLog("获取请求列表")
    public CustomResult getReqByRecordId(@RequestParam String record_id,Integer page, Integer pageSize, String sort){
        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("request");
        Long count = collection.countDocuments(Filters.eq("record_id", new ObjectId(record_id)));
        System.out.println(count);
        // 定义分页参数
        int skip = (page - 1) * pageSize;

        // 构建聚合查询
        AggregateIterable<Document> aggregation = collection.aggregate(Arrays.asList(
                Aggregates.match(Filters.eq("record_id", new ObjectId(record_id))),
                Aggregates.skip(skip),
                Aggregates.limit(pageSize)
        ));

        List<String> arraylist = new ArrayList<>();
        // 遍历查询结果
        for (Document result : aggregation) {
            String jsonpObject = result.toJson();
            arraylist.add(jsonpObject);
        }
        Map<String, Object> map = new HashMap<>();
        Stream.of(
                new AbstractMap.SimpleEntry<>("currentPage", page),
                new AbstractMap.SimpleEntry<>("pageSize", pageSize),
                new AbstractMap.SimpleEntry<>("totalOrders", count),
                new AbstractMap.SimpleEntry<>("totalPages", (count / pageSize) + 1),
                new AbstractMap.SimpleEntry<>("request", arraylist)
        ).forEach(entry -> map.put(entry.getKey(), entry.getValue()));

        return CustomResult.ok(map);

    }

    @GetMapping("/getAllRecordById")
    @OperationLog("获取记录列表")
    public CustomResult getAllRecordById(@RequestParam String task_id,Integer page, Integer pageSize, String sort){
        System.out.println(task_id);
        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("record");
        Long count = collection.countDocuments(Filters.eq("task_id", new ObjectId(task_id)));
        // 定义分页参数
        int skip = (page - 1) * pageSize;

        // 构建聚合查询
        AggregateIterable<Document> aggregation = collection.aggregate(Arrays.asList(
                Aggregates.match(Filters.eq("task_id", new ObjectId(task_id))),
                Aggregates.skip(skip),
                Aggregates.limit(pageSize)
        ));

        List<String> arraylist = new ArrayList<>();
        // 遍历查询结果
        for (Document result : aggregation) {
            String jsonpObject = result.toJson();
            arraylist.add(jsonpObject);
        }
        Map<String, Object> map = new HashMap<>();
        Stream.of(
                new AbstractMap.SimpleEntry<>("currentPage", page),
                new AbstractMap.SimpleEntry<>("pageSize", pageSize),
                new AbstractMap.SimpleEntry<>("totalOrders", count),
                new AbstractMap.SimpleEntry<>("totalPages", (count / pageSize) + 1),
                new AbstractMap.SimpleEntry<>("record", arraylist)
        ).forEach(entry -> map.put(entry.getKey(), entry.getValue()));

        return CustomResult.ok(map);

    }


    @PostMapping("/start")
    @OperationLog("开始测试")
    public CustomResult start(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String token) throws Exception{
        String task_id = payload.get("task_id");
        // post请求 url: http://49.232.65.15/api/auto_test/startTest  传入参数task_id
        // 定义远程服务的 URL
        String url = chaosMeshUrl + "/api/auto_test/startTest?task_id=" + task_id;
        // 创建 HttpHeaders 实例，并设置 Content-Type 为 application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 发起请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(null, headers);
        try {
            // 使用 RestTemplate 发起 POST 请求并接收响应
            StartTestRsp response = restTemplate.postForObject(url, requestEntity, StartTestRsp.class);
            if (response != null) {
                if (response.getStatus() == 20000) {
                    // 更新record，添加执行人
                    String userName = jwtTokenUtil.getUsernameFromToken(token.substring(7));
                    // 连接到MongoDB
                    MongoDatabase database = mongoClient.getDatabase("efficientTest");
                    MongoCollection<Document> collection = database.getCollection("record");
                    // 构建查询条件
                    Document query = new Document("_id", new ObjectId(response.getRecord_id()));
                    // 执行查询并获取单个文档
                    Document recordDocument = collection.find(query).first();
                    // 更新文档
                    recordDocument.put("executor", userName);
                    collection.replaceOne(query, recordDocument);
                    return new CustomResult(20000,"执行成功",null);
                } else {
                    return new CustomResult(40000, response.getMsg(), null);
                }
            } else {
                return new CustomResult(40000,"执行失败，响应为空",null);
            }
        } catch (Exception e) {
            return new CustomResult(40000,"执行失败",null);
        }
        // 新增record记录
//        String task_id = payload.get("task_id");
//        String userName = jwtTokenUtil.getUsernameFromToken(token);
//        // 连接到MongoDB
//        MongoDatabase database = mongoClient.getDatabase("efficientTest");
//        MongoCollection<Document> collection = database.getCollection("record");
//        // start_time、end_time、executor、status
//        // end_time往后推3分钟
//        Document recordDocument = new Document("task_id", new ObjectId(task_id))
//                .append("executor", userName)
//                .append("status", 2)
//                .append("start_time", new Date()).append("end_time", new Date(System.currentTimeMillis() + 3 * 60 * 1000));
//        collection.insertOne(recordDocument);
//        // 新增request记录
//
//        // 新增request_detail记录
//
//        return CustomResult.ok(recordDocument.get("_id").toString());
    }

    @GetMapping("/getRequestOverAll")
    @OperationLog("获取请求概览")
    public CustomResult getRequestOverAll(@RequestParam String recordId){
        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("TraceRequest");
        // 构建查询条件
        Document query = new Document("record_id", new ObjectId(recordId));
        // 执行查询并获取所有符合条件的request
        FindIterable<Document> requestDocuments = collection.find(query);
        // 获取所有的请求
        List<TestRequest> requestList = new ArrayList<>();
        for (Document doc : requestDocuments) {
            TestRequest request = documentToTestRequest(doc);
            requestList.add(request);
        }
        Map<String, Long> codeCategories = requestList.stream()
                .collect(Collectors.groupingBy(request -> {
                    int code = request.getCode(); // 获取code
                    return (code / 100) + "xx"; // 将code转换为"1xx", "2xx"等
                }, Collectors.counting())); // 计算每个分类的数量

        // 将Map转换为OverAllItem列表
        List<OverAllItem> overAll = new ArrayList<>();
        codeCategories.forEach((title, num) -> overAll.add(new OverAllItem(num.intValue(), title)));
        return new CustomResult(overAll);
    }
    // 将Document转换为TestRequest对象的方法，这需要根据TestRequest类的结构来编写
    private TestRequest documentToTestRequest(Document doc) {
        TestRequest request = new TestRequest();

        request.set_id(doc.getObjectId("_id"));
        request.setRecord_id(doc.getObjectId("record_id"));
        request.setCode(doc.getInteger("code"));
        request.setQuestion(doc.getString("question"));
        request.setSuggestion(doc.getString("suggestion"));
        request.setAnalysis(doc.getString("analysis"));
        request.setRsp_time(doc.getLong("rsp_time"));
        request.setRequest_detail_id(doc.getObjectId("request_detail_id"));
        return request;
    }

    @GetMapping("/getFaultName")
    @OperationLog("获取故障名称")
    public CustomResult getFaultName(@RequestParam String detail_id,String serviceName,int faultIndex){
        if (faultIndex < 0) {
            return new CustomResult(40000,"faultIndex不能小于0","无故障注入");
        }
        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("TraceRequest");
        // 构建查询条件
        Document query = new Document("request_detail_id", new ObjectId(detail_id));
        // 执行查询并获取单个文档
        Document requestDocument = collection.find(query).first();
        System.out.println("111: " +requestDocument);
        ObjectId record_id = requestDocument.getObjectId("record_id");
        // 构建查询条件
        Document query2 = new Document("_id", record_id);
        // 连接到MongoDB
        MongoCollection<Document> collection2 = database.getCollection("record");
        // 执行查询并获取单个文档
        Document recordDocument = collection2.find(query2).first();
        ObjectId task_id = recordDocument.getObjectId("task_id");
        // 构建查询条件
        Document query3 = new Document("_id", task_id);
        // 连接到MongoDB
        MongoCollection<Document> collection3 = database.getCollection("task");
        // 执行查询并获取单个文档
        Document taskDocument = collection3.find(query3).first();
        List<Document> faultConfigDoc = (List<Document>) taskDocument.get("fault_config");
        for (Document faultConfig : faultConfigDoc) {
            if (faultConfig.getString("service_name").equals(serviceName)) {
                List<Document> faultList = (List<Document>) faultConfig.get("fault_list");
                Document fault = faultList.get(faultIndex);
                return CustomResult.ok(fault.getString("chaosType"));
            }
        }
        return new  CustomResult(40000,"未找到",null);
    }

    // 根据id获取任务
    @GetMapping("/getSingleTaskById")
    @OperationLog("获取任务")
    public CustomResult getSingleTaskById(@RequestParam String task_id) {
        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("task");

        // 构建查询条件
        Document query = new Document("_id", new ObjectId(task_id));

        // 执行查询并获取单个文档
        Document taskDocument = collection.find(query).first();
        if (taskDocument == null) {
            return new CustomResult(40000,"Task not found",null);
        }

        // 将其中的ObjectId转换为字符串
        taskDocument.put("_id", taskDocument.getObjectId("_id").toHexString());

        // 处理file_list字段
        if (taskDocument.containsKey("file_list")) {
            List<ObjectId> file_list = taskDocument.getList("file_list", ObjectId.class);
            taskDocument.put("file_list", file_list.stream().map(ObjectId::toHexString).collect(Collectors.toList()));
        }

        // 处理fault_config字段
        if (taskDocument.containsKey("fault_config")) {
            List<Document> fault_config = taskDocument.getList("fault_config", Document.class);
            for (Document fault : fault_config) {
                if (fault.containsKey("file_id")) {
                    ObjectId fileId = fault.getObjectId("file_id");
                    fault.put("file_id", fileId.toHexString());
                }
            }
            taskDocument.put("fault_config", fault_config);
        }

        // 处理apiDoc字段
        if (taskDocument.containsKey("apiDoc")) {
            ObjectId apiDocId = taskDocument.getObjectId("apiDoc");
            taskDocument.put("apiDoc", apiDocId.toHexString());
        }

        return CustomResult.ok(taskDocument);
    }

    // 根据recordId获取所有request
    @GetMapping("/getAllRequestByRecordId")
    @OperationLog("获取请求列表")
    public CustomResult getAllRequestByRecordId(@RequestParam String recordId){
        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("TraceRequest");
        // 构建查询条件
        Document query = new Document("record_id", new ObjectId(recordId));
        // 执行查询并获取所有符合条件的request
        FindIterable<Document> requestDocuments = collection.find(query);
        // 获取所有的请求
        List<TestRequest> requestList = new ArrayList<>();
        for (Document doc : requestDocuments) {
            TestRequest request = documentToTestRequest(doc);
            requestList.add(request);
        }
        return CustomResult.ok(requestList);
    }


}

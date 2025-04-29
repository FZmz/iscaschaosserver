package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.*;
import com.iscas.lndicatormonitor.dto.*;
import com.iscas.lndicatormonitor.dto.record.PlayHostInfo;
import com.iscas.lndicatormonitor.dto.workflow.NodeInfo;
import com.iscas.lndicatormonitor.dto.workflow.TimeStampResult;
import com.iscas.lndicatormonitor.dto.workflow.WorkflowData;
import com.iscas.lndicatormonitor.service.*;
import com.iscas.lndicatormonitor.utils.GoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@RestController
@RequestMapping("/record")
public class RecordController {
    @Value("${chaos.mesh.url}")
    private String chaosMeshUrl;

    @Value("${platform.workflowaddr}")
    private String workflowaddr;

    @Value("${platform.name}")
    private String plateformName;
    private static final Logger logger = LoggerFactory.getLogger(RecordController.class);
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
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    PlanService planService;

    @Autowired
    RecordService recordService;

    @Autowired
    UsersService usersService;

    @Autowired
    ObservedIndexService observedIndexService;

    @Autowired
    ObservedcorrelationService observedcorrelationService;

    @Autowired
    ReportService reportService;



    @Autowired
    FaultconfigService faultconfigService;
    @Autowired
    FaultcorrelationService faultcorrelationService;
    @Autowired
    SelectorService selectorService;
    @Autowired
    PrometheusIndexServicenew prometheusIndexServicenew;
    @Autowired
    PrometheusIndexAnService prometheusIndexAnService;
    @Autowired
    PresscorrelationService presscorrelationService;
    @Autowired
    AsyncService asyncService;

    @Autowired
    GoUtils goUtils;
    @Autowired
    WorkflowcorrelationService workflowcorrelationService;

    @Autowired
    AddressService addressService;

    @Autowired
    PlandurationService plandurationService;

    @Autowired
    WorkflowService workflowService;


    @PostMapping("/startPlay")
    @OperationLog("开始演练")
    public CustomResult startPlay(@RequestBody StartPlayDTO startPlayDTO) throws Exception {
        int planId = startPlayDTO.getPlayId();
        Plan plan = planService.getPlanById(planId);
        // 1.如果压测文件不为空
        if (!plan.getPressContent().isEmpty()) {
            asyncService.performAsyncTasks(plan, startPlayDTO);
        } else {
            // 2.否则直接进行注入
            goUtils.toInject(startPlayDTO);
        }
        return CustomResult.ok();
    }

    @GetMapping("/getAll")
    @OperationLog("获取所有演练")
    public CustomResult getAllRecord() throws Exception {
        // 需要从数据库中取出recordList
        // 1、从recordList中取出各个record 然后根据plan_id 获取对应的plan信息
        // 2、然后根据自身的playerId获取 真实姓名
        // 3、获取所有指标名 指标名从obindexCorrelation里获取就行
        List<RecordItemDTO> recordItemDTOList = new ArrayList<>();
        List<Record> recordList = recordService.getAll();
        for (Record record : recordList) {
            RecordItemDTO recordItemDTO = new RecordItemDTO();
            List<String> faultNameList = new ArrayList<>();
            // 处理基本字段
            BeanUtils.copyProperties(record, recordItemDTO);
            recordItemDTO.setPlayer(usersService.getRealNameById(record.getPlayerId()));
            recordItemDTO.setPlanName(planService.getPlanNameById(record.getPlanId()));
            // 处理指标
            List<Observedcorrelation> observedcorrelationList = observedcorrelationService.selectByRecordId(record.getId());
            for (Observedcorrelation observedcorrelation : observedcorrelationList) {
                ObservedIndex observedIndex = observedIndexService.selectByPrimaryKey(observedcorrelation.getObservedId());
                faultNameList.add(observedIndex.getName());
            }
            recordItemDTO.setFaultNameList(faultNameList);
            recordItemDTOList.add(recordItemDTO);
        }
        return CustomResult.ok(recordItemDTOList);
    }

    @GetMapping("/getRecordsByPage")
    @OperationLog("获取演练列表")
    public CustomResult getRecordsByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) throws Exception {

        logger.info("Received request for getRecordsByPage with page=" + page + " and size=" + size);

        try {
            // 初始化分页对象
            Page<Record> pagination = new Page<>(page, size);
            logger.debug("Initialized Page object with page=" + page + ", size=" + size);

            // 分页查询记录
            Page<Record> recordPage = recordService.page(pagination);
            logger.info("Queried recordService.page, total records: " + recordPage.getTotal());

            List<Record> recordList = recordPage.getRecords();
            logger.info("Fetched " + recordList.size() + " records for the current page.");

            // 转换为 DTO 列表
            List<RecordItemDTO> recordItemDTOList = new ArrayList<>();
            for (Record record : recordList) {
                logger.debug("Processing record with ID: " + record.getId());

                RecordItemDTO recordItemDTO = new RecordItemDTO();
                List<String> faultNameList = new ArrayList<>();

                // 处理基本字段
                BeanUtils.copyProperties(record, recordItemDTO);
                logger.debug("Copied properties from Record to RecordItemDTO for record ID: " + record.getId());

                // 获取玩家真实姓名
                String playerName = usersService.getRealNameById(record.getPlayerId());
                recordItemDTO.setPlayer(playerName);
                logger.debug("Fetched player name: " + playerName + " for record ID: " + record.getId());

                // 获取计划名称
                String planName = planService.getPlanNameById(record.getPlanId());
                recordItemDTO.setPlanName(planName);
                logger.debug("Fetched plan name: " + planName + " for record ID: " + record.getId());

                // 处理指标
                List<Observedcorrelation> observedcorrelationList = observedcorrelationService.selectByRecordId(record.getId());
                logger.debug("Fetched " + observedcorrelationList.size() + " observed correlations for record ID: " + record.getId());

                for (Observedcorrelation observedcorrelation : observedcorrelationList) {
                    ObservedIndex observedIndex = observedIndexService.selectByPrimaryKey(observedcorrelation.getObservedId());
                    faultNameList.add(observedIndex.getName());
                }
                recordItemDTO.setFaultNameList(faultNameList);
                logger.debug("Processed fault names: " + faultNameList + " for record ID: " + record.getId());

                recordItemDTOList.add(recordItemDTO);
                logger.debug("Added RecordItemDTO for record ID: " + record.getId() + " to result list.");
            }

            // 封装分页结果
            Map<String, Object> result = new HashMap<>();
            result.put("total", recordPage.getTotal());
            result.put("data", recordItemDTOList);
            logger.info("Successfully prepared response with total records: " + recordPage.getTotal());

            return CustomResult.ok(result);

        } catch (Exception e) {
            logger.error("Error occurred while processing getRecordsByPage", e);
            throw e;
        }
    }
    public static double addHoursToDateAndReturnTimestamp(Date date, int hours) {
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime updatedDateTime = localDateTime.plusHours(hours);
        long epochMilli = updatedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return epochMilli / 1000.0;
    }
    public static Date convertUTCToLocalDate(String utcTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        ZonedDateTime utcDateTime = ZonedDateTime.parse(utcTime, formatter);
        ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault());
        return Date.from(localDateTime.toInstant());
    }

    /**
     * 计算传入的时间与当前时间的差值（以秒为单位），并向下取整。
     * 如果传入的时间在未来，则返回负数。
     *
     * @param date 需要计算差值的时间
     * @return 时间差（秒）
     */
    public static int getTimeDifferenceInSeconds(Date date) {
        long currentTimeMillis = System.currentTimeMillis();
        long timeDifference = currentTimeMillis - date.getTime();
        return (int) (timeDifference / 3600000);
    }

    @GetMapping("/getRecordById")
    @OperationLog("获取演练详情")
    public CustomResult getRecordById(int recordId) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // 格式应与日期字符串匹配
        String workflowName = "";
        // TODO 这里不能写死 可能得改动下数据库 dwglpt
        String nameSpace = plateformName;
        // 从workflowcorrelation里面查找workflowName
        workflowName = workflowcorrelationService.selectByRecordId(recordId).getName();
        String Url = chaosMeshUrl + "/api/real_time/workflow/" + workflowName + "/summary?namespace=" + workflowaddr;
        ResponseEntity<WorkflowData> response = restTemplate.getForEntity(Url, WorkflowData.class);
        WorkflowData workflowData;
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            workflowData = response.getBody();
        } else {
            // Handle error or throw an exception
            return new CustomResult(40000,"工作流获取失败",null);
        }
        RecordSingleDTO recordSingleDTO = new RecordSingleDTO();
        recordSingleDTO.setFaultConfigInfo(workflowData.getNodeInfoList());
        // 1、根据recordId 查询plan信息 填写基本信息
        Record record = recordService.selectByPrimaryKey(recordId);
        // 根据recordId找physicchaos 区分是物理机故障还是k8s故障
         Boolean isPhysicNode =  workflowService.getWorkflowById(workflowcorrelationService.selectByRecordId(recordId).getWorkflowId()).getContent().contains("PhysicalMachineChaos") ? true : false;
//        Date record_endTime = null;
//        if (!workflowData.getNodeInfoList().get(0).getEndTime().equals("")){
//            record_endTime = formatter.parse(workflowData.getNodeInfoList().get(0).getEndTime());
//            if (record.getEndTime() == null) record.setEndTime(record_endTime);
//        }
        Plan plan = planService.getPlanById(record.getPlanId());
        RecordSpecDTO recordSpecDTO = new RecordSpecDTO();
        recordSpecDTO.setPlanName(plan.getName());
        recordSpecDTO.setPlayer(usersService.getRealNameById(record.getPlayerId()));
        // 根据workflowData得到状态
        recordSpecDTO.setStartTime(record.getStartTime());
        // 这个endTime应该是根据最后节点的结束时间算的

        System.out.println(record.getEndTime());
        recordSpecDTO.setEndTime(record.getEndTime());
        // 计算间隔
        recordSpecDTO.setDuration(calculateDuration(record.getStartTime(), record.getEndTime() == null ? new Date(): record.getEndTime()));
        // 设置故障数
        recordSpecDTO.setFaultNum(faultcorrelationService.selectByPlanId(record.getPlanId()).size());

        // 压力状态获取的话应该从go端获取 需要准备好命名空间 workflow名称

        // 请求go端 获取执行数据 (暂时默认无压测)
        recordSpecDTO.setPressStatus(0);

        // 演练进度应该也是从go端获取
        List<NodeInfo> nodeInfoList = workflowData.getNodeInfoList();

        // 计算总数
        int totalCount = nodeInfoList.size();

        // 计算已完成数
        long completedCount = nodeInfoList.stream()
                .filter(node -> node.getEndTime() != null && !node.getEndTime().isEmpty())
                .count();
        // 设置record的状态
        // 计算进度
        int progress = (int) ((double) completedCount / totalCount * 100);

        // 设置进度
        recordSpecDTO.setPlayProgress(progress);

        // 2、获取计划的一个运行状态：压测状态、演练进度、演练详情(未开始、成功、失败、运行中) 这个也应该从go端获取
        PlayStatusInfo playStatusInfo = new PlayStatusInfo();
        // 初始化各状态的计数器
        int unStartCount = 0;
        int runningCount = 0;
        int successCount = 0;

        // 遍历nodeInfoList以确定每个NodeInfo的状态
        for (NodeInfo node : nodeInfoList) {
            if (node.getStartTime() == null || node.getStartTime().isEmpty()) {
                unStartCount++;
            } else if (node.getEndTime() == null || node.getEndTime().isEmpty()) {
                runningCount++;
            } else {
                successCount++;
            }
        }

        // 判断第一个节点的时间是否结束
//        if (!nodeInfoList.get(0).getEndTime().equals("")){
//            String endTimeStr = nodeInfoList.get(0).getEndTime();
//            Instant instant = Instant.parse(endTimeStr);  // 将字符串转换为Instant对象
//            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());  // 转换为系统默认时区的ZonedDateTime
//            Date date = Date.from(instant);  // 将Instant转换为旧的Date对象，如果你需要的话
//            recordSpecDTO.setEndTime(date);
//            // 计算间隔
//            recordSpecDTO.setDuration(calculateDuration(record.getStartTime(), date));
//            record.setRecordStatus(2);
//            recordService.updateByPrimaryKey(record);
//        }
        // 将各状态的计数设置到playStatusInfo
        playStatusInfo.setUnStart(unStartCount);
        playStatusInfo.setRunning(runningCount);
        playStatusInfo.setSuccess(successCount);
        playStatusInfo.setFailure(0);  // 因为Failure默认为0

        recordSpecDTO.setPlayStatusInfo(playStatusInfo);
        recordSingleDTO.setRecordSpecDTO(recordSpecDTO);

        // 3、graphData从plan表
        recordSingleDTO.setGraph(plan.getGraph());
        // 4、每个故障执行信息
        /*
         *   故障名
         *   故障流程图
         *   参数配置：
         *   演练指标：
         *      指标名：
         *      指标数据：
         * */
        // 设置一个故障演练信息的列表 这里边装的是每个参与演练的故障的(故障名、故障流程图、参数配置、演练指标)
        List<FaultPlayInfo> faultPlayInfoList = new ArrayList<>();
        // 4.1 首先得查询有哪些故障 从faultCorrelation里面查到 plan与faultconfig的一个联系
        List<Faultcorrelation> faultcorrelationList = faultcorrelationService.selectByPlanId(plan.getId());
        // 4.2 得到了故障id 那么就可以得到具体的故障信息
        for (Faultcorrelation faultcorrelation : faultcorrelationList) {
            // 配置单个FaultPlayInfo
            FaultPlayInfo faultPlayInfo = new FaultPlayInfo();

            // 得到具体故障信息
            Faultconfig faultconfig = faultconfigService.selectByPrimaryKey(faultcorrelation.getFaultConfigId());
            String faultconfigType = faultconfig.getFaultTypeConfig().contains("K8s") ? "K8s" : "Physic";

            faultPlayInfo.setName(faultconfig.getName());
            faultPlayInfo.setGraphData(faultconfig.getGraph());

            String targetNodeTag = faultconfig.getNodeTag();
            TimeStampResult result = new TimeStampResult();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            nodeInfoList.stream()
                    .filter(nodeInfo -> targetNodeTag.equals(nodeInfo.getNodeName()))
                    .findFirst()
                    .ifPresent(nodeInfo -> {
                        String startTime = nodeInfo.getStartTime();
                        String endTime = nodeInfo.getEndTime();
                        // 如果startTime为空字符串，那么设置startTimeStamp与endTimeStamp都为null
                        if (startTime == null || startTime.isEmpty()) {
                            // startTime即现在时刻往前推1h endTime 即当前时刻
                            result.setStartTimeStamp(addHoursToDateAndReturnTimestamp(new Date(),1));
                            result.setEndTimeStamp(addHoursToDateAndReturnTimestamp(new Date(),0));
                            return;
                        }
                        result.setStartTimeStamp(addHoursToDateAndReturnTimestamp(convertUTCToLocalDate(startTime),-1));
                        if (endTime == null || endTime.isEmpty()) {
                            // endTime为空 那么endTime就取当前时间
                            result.setEndTimeStamp(addHoursToDateAndReturnTimestamp(new Date(),0));
                        }else {
                            // 不为空 如果endTime离现在超过了1h，则时间间隔设置为1 否则就是0
                            if (getTimeDifferenceInSeconds(convertUTCToLocalDate(endTime))>1){
                                result.setEndTimeStamp(addHoursToDateAndReturnTimestamp(new Date(),1));
                            }else {
                                result.setEndTimeStamp(addHoursToDateAndReturnTimestamp(new Date(),0));
                            }
                        }

                    });

            // 具体的参数配置信息在faultTypeConfig这个字段里
            faultPlayInfo.setParamInfo(faultconfig.getFaultTypeConfig());

            if (faultconfigType.equals("K8s")){
                Selector selector = selectorService.selectByFaultConfigKey(faultconfig.getId());
                List<PlayPodInfo> playPodInfoList = new ArrayList<>();

                // 获取到所有pod名
                // FIXME 这里应该是用servicename进行查询

                List<String> podNameList = Arrays.asList(selector.getPodnames().split(","));
                String label = selector.getLabels().replace(":","=");
                String serviceName = goUtils.findSvcbyPodLabel(label,nameSpace);
                // 具体节点的信息(命名空间、podName、indexName、data) k8s节点获取指标数据的地方
                    // 一个故障可能要对多个pod进行注入 这里需要读取出Selector
                    PlayPodInfo playPodInfo = new PlayPodInfo();
                    playPodInfo.setNameSpace(selector.getNamespace());
                    // pod名称
                    playPodInfo.setPodName(serviceName);
                    // 设置具体指标名称 指标应该是当前故障配置下的所有指标 应该是一个pod对应着多个指标 然后一个指标对应着一个数据
                    List<PlayIndexInfo> playIndexInfoList = new ArrayList<>();
                    List<ObservedIndex> observedIndexList = observedIndexService.selectByFaultConfigId(faultconfig.getId());
                    // 处理系统指标
                    if (observedIndexList.size() > 0) {
                        for (ObservedIndex observedIndex : observedIndexList) {
                            PlayIndexInfo playIndexInfo = new PlayIndexInfo();
                            playIndexInfo.setIndexName(observedIndex.getName());
                            // 设置演练的数据 这个应该判断对应指标的类型
                            // 如果类型为1 则是系统数据，调用promethues获取 如果说是2 则从压测文件中获取
                            // 在压测文件中获取的需要判断是否为空
                            if (result.getStartTimeStamp() != 0) {
                                PrometheusIndex prometheusIndex = new PrometheusIndex();
                                prometheusIndex.setStartTime(result.getStartTimeStamp());
                                prometheusIndex.setEndTime(result.getEndTimeStamp());
                                prometheusIndex.setNameSpace(nameSpace);

                                prometheusIndex.setPodName(serviceName);
                                // 设置步长
                                int step = calculateStep(new Date((long) result.getStartTimeStamp() * 1000),new Date((long) result.getEndTimeStamp() * 1000));
                                prometheusIndex.setStep(step);
                                prometheusIndex.setName(observedIndex.getName());
                                Object res = prometheusIndexServicenew.getPrometheusIndex(prometheusIndex);
                                if (res != null) {
                                    playIndexInfo.setData((List<Object>) res);
                                }
                            }
                            playIndexInfoList.add(playIndexInfo);
                        }
                        playPodInfo.setPlayIndexInfo(playIndexInfoList);
                    }
                    // 处理压测指标
                    if (!plan.getPressContent().isEmpty()) {
                        List<Object> pressTestInfo = new ArrayList<>();
                        // 获取注入前的pressId
                        List<Presscorrelation> presscorrelations = presscorrelationService.selectByRecordId(recordId);
                        for (Presscorrelation presscorrelation : presscorrelations) {
                            pressTestInfo.add(getPressContent(presscorrelation.getK6Id()));
                        }
                        playPodInfo.setPressTestInfo(pressTestInfo);
                    }
                    playPodInfoList.add(playPodInfo);
                faultPlayInfo.setPlayPodInfo(playPodInfoList);
            }else{
                // 物理机节点又该如何展示呢？ 大致结构还是相同的
                // 处理物理机器故障
                List<PlayHostInfo> playHostInfoList = new ArrayList<>();
                // 获取所有地址
                Address address = addressService.selectByFaultConfigId(faultconfig.getId());
                String [] addressList = address.getAddress().split(",");
                for (String addressStr : addressList){
                    PlayHostInfo playHostInfo = new PlayHostInfo();
                    playHostInfo.setHost(addressStr);
                    // 处理系统指标
                    List<PlayIndexInfo> playIndexInfoList = new ArrayList<>();
                    List<ObservedIndex> observedIndexList = observedIndexService.selectByFaultConfigId(faultconfig.getId());
                    if (observedIndexList.size() > 0){
                        for (ObservedIndex observedIndex : observedIndexList) {
                            PlayIndexInfo playIndexInfo = new PlayIndexInfo();
                            playIndexInfo.setIndexName(observedIndex.getName());
                            if (result.getStartTimeStamp() != 0) {
                                PrometheusIndex prometheusIndex = new PrometheusIndex();
                                prometheusIndex.setStartTime(result.getStartTimeStamp());
                                prometheusIndex.setEndTime(result.getEndTimeStamp());
                                // 设置步长
                                int step = calculateStep(new Date((long) result.getStartTimeStamp() * 1000),new Date((long) result.getEndTimeStamp() * 1000));
                                prometheusIndex.setStep(step);
                                prometheusIndex.setName(observedIndex.getName());
                                Object res = prometheusIndexAnService.getPrometheusIndex(prometheusIndex);
                                if (res != null) {
                                    // nodecpu 和 nodememory单独处理
                                    // 怎么把那个instance给剥离出来呢？
                                    if (!observedIndex.getName().equals("node_CPU") && !observedIndex.getName().equals("node_memory")&&!observedIndex.getName().equals("node_load1")&&!observedIndex.getName().equals("node_cpu_sum")){
                                        Gson gson = new Gson();
                                        String json = gson.toJson(res);
                                        JSONObject jsonObject = new JSONObject(json);
                                        String dynamicKey = jsonObject.keys().next();
                                        JSONArray array = jsonObject.getJSONArray(dynamicKey);
                                        jsonObject.put("data", array);
                                        // 将JSONArray转换为Java List
                                        List<Object> list = new ArrayList<>();
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONArray innerArray = array.getJSONArray(i); // 获取内部的 JSONArray
                                            Object[] objectArray = new Object[innerArray.length()];
                                            for (int j = 0; j < innerArray.length(); j++) {
                                                // 检查类型，然后相应转换
                                                if (innerArray.get(j) instanceof Integer) {
                                                    objectArray[j] = innerArray.getInt(j); // 获取整数
                                                } else if (innerArray.get(j) instanceof String) {
                                                    objectArray[j] = innerArray.getString(j); // 获取字符串
                                                } else {
                                                    // 可以继续添加更多的判断，以处理不同的类型
                                                    objectArray[j] = innerArray.get(j); // 获取对象
                                                }
                                            }
                                            list.add(objectArray); // 将对象数组添加到列表中
                                        }
                                        playIndexInfo.setData(list);
                                    }else {
                                        playIndexInfo.setData((List<Object>) res);
                                    }
                                }
                            }
                            playIndexInfoList.add(playIndexInfo);
                        }
                        playHostInfo.setPlayIndexInfos(playIndexInfoList);
                    }
                    // 处理压测指标
                    if (!plan.getPressContent().isEmpty()) {
                        List<Object> pressTestInfo = new ArrayList<>();
                        // 获取注入前的pressId
                        List<Presscorrelation> presscorrelations = presscorrelationService.selectByRecordId(recordId);
                        for (Presscorrelation presscorrelation : presscorrelations) {
                            pressTestInfo.add(getPressContent(presscorrelation.getK6Id()));
                        }
                        playHostInfo.setPressTestInfo(pressTestInfo);
                    }
                    playHostInfoList.add(playHostInfo);
                }
                faultPlayInfo.setPlayHostInfos(playHostInfoList);
            }
            faultPlayInfoList.add(faultPlayInfo);
        }
        recordSingleDTO.setFaultPlayInfo(faultPlayInfoList);
        return CustomResult.ok(recordSingleDTO);
    }

    private double convertToTimestamp(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return 0.0;
        }
        Instant instant = Instant.parse(dateTime);
        long timestamp = instant.toEpochMilli();
        return timestamp / 1000.0; // 因为最后三位是毫秒，所以我们直接将整个数除以1000来转换为秒，并保留小数点后三位
    }

    private static String formatTimestamp(long timestamp) {
        return String.format("%d.%03d", timestamp / 1000, timestamp % 1000);
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
    public Integer toPressTest(PressTestDTO pressTestDTO) {
        String request_preT_url = chaosMeshUrl + "/api/real_time/k6/";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<PressTestDTO> entity = new HttpEntity<>(pressTestDTO, headers);
        ResponseEntity<Object> response = restTemplate.exchange(request_preT_url, HttpMethod.POST, entity, Object.class);
        return (Integer) response.getBody();
    }


    @GetMapping("/getDescById")
    @OperationLog("获取演练描述")
    public CustomResult getDescById(int recordId) throws Exception {
        return CustomResult.ok(planService.getPlanById(recordService.selectByPrimaryKey(recordId).getPlanId()).getSceneDesc());
    }

    @GetMapping("/getPlanDurationByRecordId")
    @OperationLog("获取演练时长")
    public CustomResult getWorkflowDurationById(int recordId){
        String durationStr = plandurationService.selectDurationByPlanId(recordService.selectByPrimaryKey(recordId).getPlanId());;
        Integer duration = 0;
        if (durationStr != null && !durationStr.isEmpty()) {
            duration = Integer.parseInt(durationStr.replace("s", ""));
            // 使用duration变量
        } else {
            // 处理未找到项的情况
            duration = 300;
        }
        return new CustomResult(20000,"查询成功！",duration);
    }

    public Object getPressContent(Integer k6Id) {
        String url = chaosMeshUrl + "/api/real_time/k6/" + k6Id + "/summary";
        Object result = restTemplate.getForObject(url, Object.class);
        return result;
    }

    @PostMapping("/delRecord")
    @OperationLog("删除演练")
    public CustomResult delRecord(int recordId){

        if (reportService.checkRecordIdExists(recordId) == 0){
            presscorrelationService.deleteByRecordId(recordId);

            return CustomResult.ok(recordService.deleteByPrimaryKey(recordId));
        }
        else {
            return new  CustomResult(40000,"相关报告未删除",null);
        }

    }
}

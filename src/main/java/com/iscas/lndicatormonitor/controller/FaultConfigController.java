package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.*;
import com.iscas.lndicatormonitor.dto.*;
import com.iscas.lndicatormonitor.dto.faultConfig.FaultConfigDesc;
import com.iscas.lndicatormonitor.dto.faultConfig.Pod;
import com.iscas.lndicatormonitor.dto.faultConfig.QuestPod;
import com.iscas.lndicatormonitor.service.*;
import com.iscas.lndicatormonitor.utils.FaultConfigUtils;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/faultConfig")
public class FaultConfigController {
    @Value("${chaos.mesh.token}")
    private String chaosMeshToken;


    @Autowired
    private KubernetesClient kubernetesClient;

    private static final Logger logger = Logger.getLogger(FaultConfigController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${chaos.mesh.url} ")
    private String chaosMeshUrl;

    @Autowired
    FaultconfigService faultconfigService;
    @Autowired
    FaultinnernodeService faultinnernodeService;
    @Autowired
    ObservedIndexService observedIndexService;
    @Autowired
    SelectorService selectorService;

    @Autowired
    UsersService usersService;

    @Autowired
    FaultConfigUtils faultConfigUtils;

    @Autowired
    FaultcorrelationService faultcorrelationService;

    @Autowired
    AddressService addressService;

    @Autowired
    private StateboundService stateboundService;
    @PostMapping("/faultConfigAdd")
    @OperationLog("新增故障配置")
    public CustomResult faultConfigAdd(@RequestBody FaultConfigDTO faultConfigDTO){
        // 需要操作的表有faultConfig、selector、obIndex、faultinnernode
        //  处理faultConfig
        try{
            Integer  faultConfigId = faultConfigUtils.addFaultConfig(faultConfigDTO.getBasicInfo());
            if (faultConfigId != 0){
                SelectorDTO selectorInfo = faultConfigDTO.getSelectorInfo();
                Selector selector = new Selector();
                selector.setFaultConfigId(faultConfigId);
                selector.setNamespace(selectorInfo.getNamespace());
                selector.setLabels(String.join(",",selectorInfo.getLabel()));
                selector.setPodnames(String.join(",",selectorInfo.getPodNames()));
                // 插入selector
                selectorService.insert(selector);
                if (faultConfigDTO.getSteadyIdList() != null && !faultConfigDTO.getSteadyIdList().isEmpty()){
                    for (String steadyId : faultConfigDTO.getSteadyIdList()){
                        Statebound statebound = new Statebound();
                        // 稳态类型为局部
                        statebound.setBoundType(1);
                        // 这里存faultname，相当于fault的id，也是唯一标识符
                        statebound.setBoundId(String.valueOf(faultConfigDTO.getBasicInfo().getName()));
                        statebound.setSteadyId(steadyId);
                        stateboundService.save(statebound);
                    }
                }
            }else {
                return new CustomResult(40000,"新增失败！",null);
            }
            // 插入obsevedindex
            faultConfigUtils.addObsevedindexes(faultConfigDTO.getIndexesArrDTO(),faultConfigId);
            // 插入faultinnernode
            faultConfigUtils.addFaultinnernode(faultConfigDTO.getFaultConfigNodes(),faultConfigId);
            return CustomResult.ok();
        }catch (Exception e){
            return new CustomResult(40000,"新增失败！",null);
        }
    }

    @PostMapping("/delFaultConfig")
    @OperationLog("删除故障配置")
    public CustomResult delFaultConfig(int faultConfigId){

        if (faultcorrelationService.checkFaultConfigIdExists(faultConfigId)   == 0){


            faultinnernodeService.deleteByFaultConfigId(faultConfigId);
            return CustomResult.ok(faultconfigService.deleteByPrimaryKey(faultConfigId));
        }
        else {
            return new  CustomResult(40000,"相关计划未删除",null);
        }
    }

    @PostMapping("/faultConfigPhysicalAdd")
    @OperationLog("新增物理机故障配置")
    public CustomResult faultConfigPhysicalAdd(@RequestBody FaultConfigPhysicalDTO faultConfigPhysicalDTO){
        // 需要操作的表有faultConfig、selector、obIndex、faultinnernode
        //  处理faultConfig
        try{
            Integer  faultConfigId = faultConfigUtils.addFaultConfig(faultConfigPhysicalDTO.getBasicInfo());
            if (faultConfigId != 0){
                // 处理Address
                String addressStr = faultConfigPhysicalDTO.getAddressDTO().entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + ":" + entry.getValue())
                        .collect(Collectors.joining(", "));
                Address address = new Address();
                address.setAddress(addressStr);
                address.setFaultConfigId(faultConfigId);
                addressService.insert(address);
                if (faultConfigPhysicalDTO.getSteadyIdList() != null && !faultConfigPhysicalDTO.getSteadyIdList().isEmpty()){
                    for (String steadyId : faultConfigPhysicalDTO.getSteadyIdList()){
                        Statebound statebound = new Statebound();
                        // 稳态类型为局部
                        statebound.setBoundType(2);
                        // 这里存faultname，相当于fault的id，也是唯一标识符
                        statebound.setBoundId(String.valueOf(faultConfigPhysicalDTO.getBasicInfo().getName()));
                        statebound.setSteadyId(steadyId);
                        stateboundService.save(statebound);
                    }
                }
            }else {
                return new CustomResult(40000,"新增失败！",null);
            }
            // 插入obsevedindex
            faultConfigUtils.addObsevedindexes(faultConfigPhysicalDTO.getIndexesArrDTO(),faultConfigId);
            // 插入faultinnernode
            faultConfigUtils.addFaultinnernode(faultConfigPhysicalDTO.getFaultConfigNodes(),faultConfigId);
            return CustomResult.ok();
        }catch (Exception e){
            return new CustomResult(40000,"新增失败！",e);
        }
    }
    @GetMapping("/getAllFaultConfig")
    @OperationLog("获取所有故障配置")
    public CustomResult getAllFaultConfig() throws Exception {
        // 获取所有faultconfig
        List<Faultconfig> faultconfigList = faultconfigService.selectAll();
        List<SimpleFaultConfigInfo> simpleFaultConfigInfoList = new ArrayList<>();
        for (Faultconfig faultconfig : faultconfigList){
            // 处理基本信息
            SimpleFaultConfigInfo simpleFaultConfigInfo = new SimpleFaultConfigInfo();
            BeanUtils.copyProperties(faultconfig,simpleFaultConfigInfo);
            simpleFaultConfigInfo.setCreatorName(usersService.getRealNameById(faultconfig.getCreatorId()));
            // 处理指标信息
            List<ObservedIndex> observedIndexList = observedIndexService.selectByFaultConfigId(faultconfig.getId());
            List<String> systemIndexesList = new ArrayList<>();
            List<String> pressIndexesList = new ArrayList<>();
            for (ObservedIndex observedIndex : observedIndexList){
                if (observedIndex.getType() == 1){
                    systemIndexesList.add(observedIndex.getName());
                } else if (observedIndex.getType() == 2) {
                    pressIndexesList.add(observedIndex.getName());
                }
            }
            String[] systemIndexes = systemIndexesList.toArray(new String[0]);
            String[] pressIndexes = pressIndexesList.toArray(new String[0]);
            IndexesArrDTO indexesArrDTO = new IndexesArrDTO();
            indexesArrDTO.setSystemIndexes(systemIndexes);
            indexesArrDTO.setPressIndexes(pressIndexes);
            simpleFaultConfigInfo.setIndexesArrDTO(indexesArrDTO);
            // 整合至simpleFaultConfigInfoList
            simpleFaultConfigInfoList.add(simpleFaultConfigInfo);
        }
        return CustomResult.ok(simpleFaultConfigInfoList);
    }

    @GetMapping("/getFaultConfigByPage")
    @OperationLog("获取故障配置列表")
    public CustomResult getFaultConfigByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) throws Exception {

        logger.info("Received request for getFaultConfigByPage with page=" + page + " and size=" + size);

        try {
            // 初始化分页对象
            Page<Faultconfig> pagination = new Page<>(page, size);
            logger.debug("Initialized Page object with page=" + page + ", size=" + size);

            // 分页查询 faultconfig 列表
            Page<Faultconfig> faultconfigPage = faultconfigService.page(pagination);
            logger.info("Queried faultconfigService.page, total records: " + faultconfigPage.getTotal());

            List<Faultconfig> faultconfigList = faultconfigPage.getRecords();
            logger.info("Fetched " + faultconfigList.size() + " records for the current page.");

            // 处理分页结果并转换为 SimpleFaultConfigInfo 列表
            List<SimpleFaultConfigInfo> simpleFaultConfigInfoList = new ArrayList<>();
            for (Faultconfig faultconfig : faultconfigList) {
                logger.debug("Processing Faultconfig with ID: " + faultconfig.getId());

                // 处理基本信息
                SimpleFaultConfigInfo simpleFaultConfigInfo = new SimpleFaultConfigInfo();
                BeanUtils.copyProperties(faultconfig, simpleFaultConfigInfo);
                logger.debug("Copied properties from Faultconfig to SimpleFaultConfigInfo for ID: " + faultconfig.getId());

                String creatorName = usersService.getRealNameById(faultconfig.getCreatorId());
                simpleFaultConfigInfo.setCreatorName(creatorName);
                logger.debug("Fetched creator name: " + creatorName + " for Faultconfig ID: " + faultconfig.getId());

                // 处理指标信息
                List<ObservedIndex> observedIndexList = observedIndexService.selectByFaultConfigId(faultconfig.getId());
                logger.debug("Fetched " + observedIndexList.size() + " ObservedIndexes for Faultconfig ID: " + faultconfig.getId());

                List<String> systemIndexesList = new ArrayList<>();
                List<String> pressIndexesList = new ArrayList<>();
                for (ObservedIndex observedIndex : observedIndexList) {
                    if (observedIndex.getType() == 1) {
                        systemIndexesList.add(observedIndex.getName());
                    } else if (observedIndex.getType() == 2) {
                        pressIndexesList.add(observedIndex.getName());
                    }
                }
                logger.debug("Processed system indexes: " + systemIndexesList + " and press indexes: " + pressIndexesList);

                String[] systemIndexes = systemIndexesList.toArray(new String[0]);
                String[] pressIndexes = pressIndexesList.toArray(new String[0]);
                IndexesArrDTO indexesArrDTO = new IndexesArrDTO();
                indexesArrDTO.setSystemIndexes(systemIndexes);
                indexesArrDTO.setPressIndexes(pressIndexes);
                simpleFaultConfigInfo.setIndexesArrDTO(indexesArrDTO);

                // 添加到最终结果列表
                simpleFaultConfigInfoList.add(simpleFaultConfigInfo);
                logger.debug("Added SimpleFaultConfigInfo for Faultconfig ID: " + faultconfig.getId() + " to result list.");
            }

            // 封装分页结果
            Map<String, Object> result = new HashMap<>();
            result.put("total", faultconfigPage.getTotal());
            result.put("data", simpleFaultConfigInfoList);
            logger.info("Successfully prepared response with total records: " + faultconfigPage.getTotal());

            return CustomResult.ok(result);

        } catch (Exception e) {
            logger.error("Error occurred while processing getFaultConfigByPage", e);
            throw e;
        }
    }

    @PostMapping("/faultConfigUpdate")
    @OperationLog("更新故障配置")
    public CustomResult faultConfigUpdate(@RequestBody FaultConfigDTO faultConfigDTO){
        // 更新故障配置
        // 更新基本故障配置信息
        Faultconfig faultconfig = new Faultconfig();
        Faultconfig faultconfig1 = faultconfigService.selectByPrimaryKey(faultConfigDTO.getBasicInfo().getId());
        BeanUtils.copyProperties(faultConfigDTO.getBasicInfo(),faultconfig);
        faultconfig.setCreatorId(faultconfig1.getCreatorId());

        faultconfigService.updateByPrimaryKeySelective(faultconfig);
        // 更新选择器信息
        // 这里需要做判断 是K8s 故障 还是物理机故障
        SelectorDTO selectorInfo = faultConfigDTO.getSelectorInfo();
        Selector selector = selectorService.selectByFaultConfigKey(faultconfig.getId());
        selector.setNamespace(selectorInfo.getNamespace());
        selector.setLabels(selectorInfo.getLabel());
        selector.setPodnames(String.join(",",selectorInfo.getPodNames()));

        // 更新指标信息  这个很难整 有增有减 需要依次判断
        // 获取所有的指标 与现有的指标做对比，如果说以前的指标没有在现有的指标列表里边则删去 如果现有的在以前指标列表的没出现过 则新增
        IndexesArrDTO indexesArrDTO_New = faultConfigDTO.getIndexesArrDTO(); // 最新指标列表

        List<ObservedIndex> observedIndexList = observedIndexService.selectByFaultConfigId(faultconfig.getId());
        /*
        * 注意还要考虑与record的关系
        * */
        synchronizeIndexes(indexesArrDTO_New,observedIndexList,faultconfig);

        // 更新内部节点信息
        List<FaultConfigNodes> faultConfigNodesList = faultConfigDTO.getFaultConfigNodes();

        // 有没有id呢 一般是没有id的 那么先把之前的都删了 然后再插入新的
        faultinnernodeService.deleteByFaultConfigId(faultconfig.getId());

        for (FaultConfigNodes faultConfigNodes : faultConfigNodesList){
            Faultinnernode faultinnernode = new Faultinnernode();
            faultinnernode.setFaultConfigId(faultconfig.getId());
            faultinnernode.setNodeStatus(0);
            BeanUtils.copyProperties(faultConfigNodes,faultinnernode);
            faultinnernodeService.insert(faultinnernode);
        }
        return CustomResult.ok();
    }

    @PostMapping("/faultConfigPhysicUpdate")
    @OperationLog("更新物理机故障配置")
    public CustomResult faultConfigPhysicUpdate(@RequestBody FaultConfigPhysicalDTO faultConfigPhysicalDTO){
        // 更新故障配置
        // 更新基本故障配置信息
        try{
            int faultConfigId = faultConfigPhysicalDTO.getBasicInfo().getId();
            Faultconfig faultconfig = new Faultconfig();
            Faultconfig faultconfig1 = faultconfigService.selectByPrimaryKey(faultConfigId);
            BeanUtils.copyProperties(faultConfigPhysicalDTO.getBasicInfo(),faultconfig);
            faultconfig.setCreatorId(faultconfig1.getCreatorId());
            // 处理Address更新
            // 处理Address
            String addressStr = faultConfigPhysicalDTO.getAddressDTO().entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .collect(Collectors.joining(", "));

            Address address = addressService.selectByFaultConfigId(faultConfigId);
            address.setAddress(addressStr);
            addressService.updateByPrimaryKey(address);
            // 更新内部节点信息
            List<FaultConfigNodes> faultConfigNodesList = faultConfigPhysicalDTO.getFaultConfigNodes();
            // 有没有id呢 一般是没有id的 那么先把之前的都删了 然后再插入新的

            faultinnernodeService.deleteByFaultConfigId(faultconfig.getId());
            for (FaultConfigNodes faultConfigNodes : faultConfigNodesList){
                Faultinnernode faultinnernode = new Faultinnernode();
                faultinnernode.setFaultConfigId(faultconfig.getId());
                faultinnernode.setNodeStatus(0);
                BeanUtils.copyProperties(faultConfigNodes,faultinnernode);
                faultinnernodeService.insert(faultinnernode);
            }
            faultconfigService.updateByPrimaryKeySelective(faultconfig);
            return CustomResult.ok();
        }catch (Exception e){
            return new CustomResult(40000,"更新失败",e);
        }
    }
    public void synchronizeIndexes(IndexesArrDTO indexesArrDTO_New, List<ObservedIndex> observedIndexList,Faultconfig faultconfig) {
        // 将observedIndexList转换为两个Set，以方便比较
        Set<String> observedSystemIndexes = observedIndexList.stream()
                .filter(index -> index.getType() == 1)
                .map(ObservedIndex::getName)
                .collect(Collectors.toSet());

        Set<String> observedPressIndexes = observedIndexList.stream()
                .filter(index -> index.getType() == 2)
                .map(ObservedIndex::getName)
                .collect(Collectors.toSet());

        // 遍历systemIndexes, 检查与observedSystemIndexes的差异
        for (String index : indexesArrDTO_New.getSystemIndexes()) {
            if (!observedSystemIndexes.contains(index)) {
                // 新系统指标，需要添加
                IndexsDTO indexsDTO = new IndexsDTO();
                indexsDTO.setName(index);
                indexsDTO.setType(1);
                indexsDTO.setFaultConfigId(faultconfig.getId());  // 注意: 这里需要确保faultconfig已经初始化
                observedIndexService.insert(indexsDTO);
            }
        }

        // 遍历pressIndexes, 检查与observedPressIndexes的差异
        for (String index : indexesArrDTO_New.getPressIndexes()) {
            if (!observedPressIndexes.contains(index)) {
                // 新压测指标，需要添加
                IndexsDTO indexsDTO = new IndexsDTO();
                indexsDTO.setName(index);
                indexsDTO.setType(2);
                indexsDTO.setFaultConfigId(faultconfig.getId());  // 注意: 这里需要确保faultconfig已经初始化
                observedIndexService.insert(indexsDTO);
            }
        }

        // 检查observedIndexList中是否有需要删除的项
        for (ObservedIndex observedIndex : observedIndexList) {
            if (observedIndex.getType() == 1 && !Arrays.asList(indexesArrDTO_New.getSystemIndexes()).contains(observedIndex.getName())) {
                // 删除此系统指标
                observedIndexService.deleteByPrimaryKey(observedIndex.getId());
            } else if (observedIndex.getType() == 2 && !Arrays.asList(indexesArrDTO_New.getPressIndexes()).contains(observedIndex.getName())) {
                // 删除此压测指标
                observedIndexService.deleteByPrimaryKey(observedIndex.getId());
            }
        }
    }

    @GetMapping("/getFaultConfigById")
    @OperationLog("获取故障配置详情")
    public CustomResult getFaultConfigById(int faultConfigId) throws Exception {
        FaultConfigSingleDTO faultConfigDTO = new FaultConfigSingleDTO();
        Faultconfig faultconfig =  faultconfigService.selectByPrimaryKey(faultConfigId);
        // 处理basicInfo
        BasicInfo basicInfo = new BasicInfo();
        BeanUtils.copyProperties(faultconfig,basicInfo);
        basicInfo.setCreator(usersService.getRealNameById(faultconfig.getCreatorId()));
        // 处理selector
        // 需要区分K8s故障和物理机故障
        if (faultconfig.getFaultTypeConfig().contains("K8s")){
            SelectorDTO selectorDTO = new SelectorDTO();
            Selector selector = selectorService.selectByFaultConfigKey(faultConfigId);
            selectorDTO.setNamespace(selector.getNamespace());
            selectorDTO.setLabel(selector.getLabels());
            selectorDTO.setPodNames(selector.getPodnames().split(","));
            faultConfigDTO.setSelectorInfo(selectorDTO);
        }else if(faultconfig.getFaultTypeConfig().contains("Physic")){
            // 这里后续肯定会考虑多个地址的情况
            AddressDTO addressDTO = new AddressDTO();
            Address address = addressService.selectByFaultConfigId(faultConfigId);
            BeanUtils.copyProperties(address,addressDTO);
            String[] addressPairs = address.getAddress().split(",");

            // 创建 HashMap
            HashMap<String, String> addressMap = new HashMap<>();

            // 循环数组，将每个 IP:Port 对拆分开并添加到 HashMap 中
            for (String pair : addressPairs) {
                String[] parts = pair.split(":");
                if (parts.length == 2) {
                    // 使用 IP 地址作为键，端口作为值
                    addressMap.put(parts[0], parts[1]);
                }
            }
            addressDTO.setAddress(addressMap);
            faultConfigDTO.setAddressInfo(addressDTO);
        }
        List<String> stateIdList = stateboundService.getSteadyIdsByBoundId(String.valueOf(faultconfig.getId()));
        if (stateIdList.size() > 0){
            faultConfigDTO.setSteadyIdList(stateIdList);
        }
        // 处理指标
        List<ObservedIndex> observedIndexList = observedIndexService.selectByFaultConfigId(faultconfig.getId());
        List<String> systemIndexesList = new ArrayList<>();
        List<String> pressIndexesList = new ArrayList<>();

        for (ObservedIndex observedIndex : observedIndexList){
            if (observedIndex.getType() == 1){
                systemIndexesList.add(observedIndex.getName());
            } else if (observedIndex.getType() == 2) {
                pressIndexesList.add(observedIndex.getName());
            }
        }
        String[] systemIndexes = systemIndexesList.toArray(new String[0]);
        String[] pressIndexes = pressIndexesList.toArray(new String[0]);
        IndexesArrDTO indexesArrDTO = new IndexesArrDTO();
        indexesArrDTO.setSystemIndexes(systemIndexes);
        indexesArrDTO.setPressIndexes(pressIndexes);

         // 处理节点
        List<Faultinnernode> faultinnernodeList = faultinnernodeService.selectByFaultConfigId(faultConfigId);
        List<FaultConfigNodes> faultConfigNodesList = new ArrayList<>();
        for (Faultinnernode faultinnernode : faultinnernodeList){
            FaultConfigNodes faultConfigNodes = new FaultConfigNodes();
            BeanUtils.copyProperties(faultinnernode,faultConfigNodes);
            faultConfigNodesList.add(faultConfigNodes);
        }
        faultConfigDTO.setBasicInfo(basicInfo);
        faultConfigDTO.setIndexesArrDTO(indexesArrDTO);
        faultConfigDTO.setFaultConfigNodes(faultConfigNodesList);
        return CustomResult.ok(faultConfigDTO);
    }
    @GetMapping("/getFaultNodeSpec")
    @OperationLog("获取故障配置节点列表")
    public CustomResult getFaultNodeSpec() throws JsonProcessingException {
        List<Faultconfig> faultconfigList = faultconfigService.selectAll();
        return CustomResult.ok(transformFaultConfig(faultconfigList));
    }
    @GetMapping("/getFaultDescById")
    @OperationLog("获取故障配置描述")
    public CustomResult getFaultDescById(int faultId) throws Exception {
        String url = chaosMeshUrl + "/api/common/pods";
        try {
            Faultconfig faultconfig = faultconfigService.selectByPrimaryKey(faultId);
            FaultConfigDesc faultConfigDesc = new FaultConfigDesc();
            faultConfigDesc.setCreateTime(faultconfig.getCreateTime());
            faultConfigDesc.setCreator(usersService.getRealNameById(faultconfig.getCreatorId()));
            // 处理一级故障类型
            String faultFirstType = faultconfig.getFaultTypeConfig().contains("K8s")? "K8s": "Physic";
            faultConfigDesc.setFaultFirstType(faultFirstType);
            if (faultFirstType.equals("K8s")){
                // 处理namespace 并获取label 请求go端得到pod数据
                // 目前固定为一个ns
                String nameSpace = selectorService.selectByFaultConfigKey(faultconfig.getId()).getNamespace();
                String labelsStr = selectorService.selectByFaultConfigKey(faultconfig.getId()).getLabels();
                faultConfigDesc.setNamespace(nameSpace);
                QuestPod questPod = new QuestPod();
                questPod.setNamespaces(Arrays.asList(nameSpace));

                // 处理labels
                Map<String,String> labels = new HashMap<>();
                String[] labelsArr = labelsStr.split(":");

                if (labelsArr.length == 2){
                    labels.put(labelsArr[0], labelsArr[1]);
                }
                labels.put(labelsArr[0], labelsArr[1]);
                questPod.setLabelSelectors(labels);
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", this.chaosMeshToken);
                // 创建请求实体对象
                HttpEntity<QuestPod> requestEntity = new HttpEntity<>(questPod, headers);

                // 使用RestTemplate发送POST请求
                ResponseEntity<List<Pod>> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<List<Pod>>() {}
                );

                List<Pod> podList = response.getBody();
                String podNames = podList.stream()
                        .map(Pod::getName)
                        .collect(Collectors.joining(", "));
                // 将结果设置到 faultConfigDesc 对象中
                faultConfigDesc.setPodName(podNames);
            }else {
                // 处理address
                Address address = addressService.selectByFaultConfigId(faultconfig.getId());
                faultConfigDesc.setAddress(address.getAddress());
            }
            return new CustomResult(20000,"获取故障描述信息成功",faultConfigDesc);
        }catch (Exception e){
            return  new CustomResult(40000,"获取故障描述信息失败",null);
        }
    }
    @GetMapping("/queryFaultNodeSpec")
    @OperationLog("获取故障配置节点列表")
    public CustomResult queryFaultNodeSpec(@RequestParam(required = false, defaultValue = "") String faultName, @RequestParam(required = false) List<String> indexList) throws JsonProcessingException {
        /*
        * 根据faultName与indexList查询对应的故障
        * 首先得确认优先级
        * 先模糊查询具有*** 名称的故障
        * 然后再过滤这些故障 根据indexList进行过滤 具体的过滤方法是根据faultId在index表里面查询 然后如果说其故障一一对应了 那么就
        * */
        // 如果faultName为空则查询全部的Faultconfig
        if (indexList == null) {
            indexList = new ArrayList<>();
        }
        List<Faultconfig> faultconfigList;
        if (faultName.isEmpty()){
            faultconfigList = faultconfigService.selectAll();

        }else {
            faultconfigList = faultconfigService.selectFaultconfigByName(faultName);
        }
        // 如果indexList长度还为0的话就直接返回
        if (indexList.size() == 0){
            return CustomResult.ok(transformFaultConfig(faultconfigList));
        }
        if (faultconfigList.size()>0){
            for (Faultconfig faultconfig: faultconfigList){
                List<ObservedIndex> observedIndexList = observedIndexService.selectByFaultConfigId(faultconfig.getId());
                Set<String> observedIndexNames = observedIndexList.stream()
                        .map(ObservedIndex::getName)
                        .collect(Collectors.toSet());
                Boolean isIn = isContained(observedIndexNames,indexList);
                if (!isIn){
                    // 此故障配置并没有包含对应的指标 所以需要过滤掉
                    faultconfigList.remove(faultconfig);
                }
            }
        }
        // 过滤完成之后需要包装返回内容

        return CustomResult.ok(transformFaultConfig(faultconfigList));
    }

    @GetMapping("/getFaultTagByName")
    @OperationLog("获取故障标签")
    public CustomResult getFaultTagByName(String faultName){
        try {
            return new CustomResult(20000,"查询成功",faultconfigService.selectFaultTagByName(faultName));
        }catch (Exception e){
            return new CustomResult(40000,"查询失败",e);
        }
    }
    private Boolean isContained(Set<String> observedIndexNames,List<String> indexList){
        for (String indexName : indexList) {
            if (!observedIndexNames.contains(indexName)) {
                return false;
            }
        }
        return true;
    }
    private List<FaultNodeSpecDTO> transformFaultConfig(List<Faultconfig> faultconfigList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<FaultNodeSpecDTO> faultNodeSpecDTOList = new ArrayList<>();
        for (Faultconfig faultconfig : faultconfigList){
            FaultNodeSpecDTO faultNodeSpecDTO = new FaultNodeSpecDTO();
            faultNodeSpecDTO.setId(faultconfig.getId());
            faultNodeSpecDTO.setFaultName(faultconfig.getName());
            faultNodeSpecDTO.setName(faultconfig.getNodeTag());
            faultNodeSpecDTO.setTemplateType("FaultConfig");
            faultNodeSpecDTO.setDeadline("1h");
            List<Faultinnernode> faultinnernodeList = faultinnernodeService.selectByFaultConfigId(faultconfig.getId());
            List<Object> innerNodeContentList = new ArrayList<>();
            for (Faultinnernode faultinnernode : faultinnernodeList){
                Object nodeContent = objectMapper.readValue(faultinnernode.getContent(), Object.class);
                innerNodeContentList.add(nodeContent);
            }
            faultNodeSpecDTO.setList(innerNodeContentList);
            faultNodeSpecDTOList.add(faultNodeSpecDTO);
        }
        return faultNodeSpecDTOList;
    }

    @GetMapping("/getTagByServiceName")
    @OperationLog("获取故障标签")
    public CustomResult getTagByServiceName(@RequestParam(value = "serviceName") String serviceName,
                                            @RequestParam(value = "namespace") String namespace) {
        logger.info("Received request to get selector for service: " + serviceName + " in namespace: " + namespace);
        try {
            // 调用 Kubernetes API 获取服务信息
            logger.debug("Fetching service details from Kubernetes API...");
            Service service = kubernetesClient.services().inNamespace(namespace).withName(serviceName).get();
            if (service == null) {
                logger.warn("Service not found: " + serviceName + " in namespace: " + namespace);
                return CustomResult.fail("Service not found: " + serviceName);
            }

            // 从服务规范中获取 selector
            Map<String, String> selector = service.getSpec().getSelector();
            if (selector != null && !selector.isEmpty()) {
                logger.info("Selector found for service: " + serviceName + ": " + selector);
                return CustomResult.ok(selector);
            } else {
                logger.warn("No selector found for service: " + serviceName);
                return CustomResult.fail("No selector found for the service.");
            }
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching service selector.", e);
            return CustomResult.fail("Unexpected error: " + null);
        }
    }
}

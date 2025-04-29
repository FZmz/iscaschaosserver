package com.iscas.lndicatormonitor.utils;

import com.alibaba.fastjson.JSON;
import com.iscas.lndicatormonitor.dto.planv2.RecordInfoDTO;
import com.iscas.lndicatormonitor.dto.workflow.WorkflowSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Component
@Slf4j
public class WorkflowUtils {
    @Value("${chaos.mesh.url}")
    private String chaosMeshUrl;

    @Value("${chaos.mesh.token}")
    private String chaosMeshToken;

    @Autowired
    private RestTemplate restTemplate;

    public void executeWorkflow(String content, String workflowName) {
        log.info("开始执行工作流，工作流名称：{}", workflowName);
        log.debug("工作流执行URL：{}", chaosMeshUrl + "/api/real_time/workflow");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", chaosMeshToken);
            log.debug("已设置请求头，Content-Type: {}", headers.getContentType());

            String newContent = content.replace("workflowname", workflowName);
            log.debug("工作流内容替换完成，替换workflowname为：{}", workflowName);

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("workflow", JSON.parse(newContent));
            log.debug("请求体封装完成");

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestMap, headers);
            log.info("发送工作流执行请求");
            restTemplate.exchange(chaosMeshUrl + "/api/real_time/workflow",
                    HttpMethod.POST, requestEntity, String.class);
            log.info("工作流执行请求发送成功");
        } catch (Exception e) {
            log.error("工作流执行请求失败，工作流名称：{}，异常信息：{}", workflowName, e.getMessage(), e);
            throw e;
        }
    }

    public WorkflowSummary getWorkflowSummary(String workflowName) {
        log.info("开始获取工作流摘要信息，工作流名称：{}", workflowName);
        String url = chaosMeshUrl + "/api/real_time/workflow/{workflowName}/summary?namespace=sock-shop";
        log.debug("工作流摘要请求URL：{}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", chaosMeshToken);
            log.debug("已设置请求头");

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            log.info("发送工作流摘要获取请求");
            ResponseEntity<WorkflowSummary> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    WorkflowSummary.class,
                    workflowName
            );

            WorkflowSummary summary = response.getBody();
            log.info("工作流摘要信息获取成功，节点数量：{}",
                    summary != null && summary.getNodeInfoList() != null ? summary.getNodeInfoList().size() : 0);
            return summary;
        } catch (Exception e) {
            log.error("获取工作流摘要失败，工作流名称：{}，异常信息：{}", workflowName, e.getMessage(), e);
            throw e;
        }
    }

    public boolean isWorkflowCompleted(WorkflowSummary summary) {
        log.debug("检查工作流是否完成");
        if (summary == null || summary.getNodeInfoList() == null) {
            log.warn("工作流摘要或节点列表为空");
            return false;
        }

        boolean completed = summary.getNodeInfoList().stream()
                .allMatch(node -> node.getEndTime() != null);
        log.info("工作流完成状态检查结果：{}", completed);
        return completed;
    }

    public boolean isWorkflowSuccessful(WorkflowSummary summary) {
        log.debug("检查工作流是否成功执行");
        if (!isWorkflowCompleted(summary)) {
            log.warn("工作流未完成，判定为非成功状态");
            return false;
        }

        for (WorkflowSummary.NodeInfo node : summary.getNodeInfoList()) {
            if (node.getReason() == null) {
                log.warn("节点[{}]的执行原因为null", node.getNodeName());
                return false;
            }

            if (!"正常完成".equals(node.getReason())) {
                log.warn("节点[{}]未正常完成，执行原因: {}", node.getNodeName(), node.getReason());
                return false;
            }

            log.info("节点[{}]执行正常，原因: {}", node.getNodeName(), node.getReason());
        }

        log.info("所有节点均正常完成");
        return true;
    }

    public RecordInfoDTO fillRecordInfo(String workflowName) {
        log.info("开始填充记录信息，工作流名称：{}", workflowName);
        RecordInfoDTO recordInfoDTO = new RecordInfoDTO();
        try {
            WorkflowSummary summary = getWorkflowSummary(workflowName);
            if (summary == null || summary.getNodeInfoList() == null || summary.getNodeInfoList().isEmpty()) {
                log.warn("工作流摘要为空或节点列表为空");
                return null;
            }
            // 获取第一个节点信息
            WorkflowSummary.NodeInfo firstNode = summary.getNodeInfoList().get(0);
            
            // 设置开始时间和结束时间
            recordInfoDTO.setStartTime(!firstNode.getStartTime().isEmpty() ? firstNode.getStartTime() : null);
            recordInfoDTO.setEndTime(!firstNode.getEndTime().isEmpty() ? firstNode.getEndTime() : null);

            // 计算故障注入对象数量
            long faultCount = summary.getNodeInfoList().stream()
                    .filter(node -> node.getNodeName() != null && node.getNodeName().startsWith("fault-"))
                    .count();
            recordInfoDTO.setFaulInjectObjectNum((int) faultCount);

            // 设置记录状态
            String reason = firstNode.getReason();
            int status;
            switch (reason) {
                case "":
                    status = 1; // 运行中
                    break;
                case "正常完成":
                    status = 2;
                    break;
                case "节点超时":
                case "手动中止":
                    status = 3;
                    break;
                default:
                    status = 1;
                    log.warn("未知的reason状态：{}", reason);
            }
            recordInfoDTO.setRecordStatus(status);
            log.info("记录信息填充完成，状态：{}，故障注入数量：{}", status, faultCount);
            return recordInfoDTO;
        } catch (Exception e) {
            log.error("填充记录信息失败，工作流名称：{}，异常信息：{}", workflowName, e.getMessage(), e);
            throw e;
        }
    }
}
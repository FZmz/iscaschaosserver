package com.iscas.lndicatormonitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Nodeagent;
import com.iscas.lndicatormonitor.dto.faultAgent.NodeagentDTO;
import com.iscas.lndicatormonitor.service.NodeagentService;
import com.iscas.lndicatormonitor.service.SSHService;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nodeagent")
@Slf4j
public class NodeAgentController {

    @Autowired
    private NodeagentService nodeagentService;

    @Autowired
    private SSHService sshService;


    @PostMapping("/install")
    @OperationLog("安装Chaosd")
    public CustomResult installChaosd(@RequestParam Long agentId) {
        try {
            Nodeagent agent = nodeagentService.getById(agentId);

            String host = agent.getAgentIp();
            String user = agent.getAgentUsr();
            String password = agent.getAgentPwd();
            // 获取当前代码目录下的资源文件
            String remoteDir = "/data/mj/chaosd";

            sshService.uploadAndInstall(host, user, password, remoteDir);

            return CustomResult.ok("Chaosd installed successfully on agent " + agentId);
        } catch (Exception e) {
            e.printStackTrace();
            return CustomResult.fail("Installation failed: " + null);
        }
    }

    @PostMapping("/uninstall")
    @OperationLog("卸载Chaosd")
    public CustomResult uninstallChaosd(@RequestParam Long agentId) {
        try {

            Session session = null;
            try {
                // 根据 agentId 获取代理信息
                Nodeagent agent = nodeagentService.getById(agentId);

                // 创建 SSH session
                session = sshService.createSession(agent.getAgentIp(), agent.getAgentUsr(), agent.getAgentPwd());

                boolean isRunning = sshService.isChaosdRunning(session);
                if(isRunning){
                    String command = "sudo kill -9 $(lsof -i:31767 -t)";
                    sshService.executeCommand(session, command);
                    return CustomResult.ok("Chaosd uninstalled successfully on agent " + agentId);
                }
            }finally {
                // 关闭 session
                sshService.closeSession(session);}
            return CustomResult.fail("Chaosd uninstalled failed on agent " + agentId);
        } catch (Exception e) {
            e.printStackTrace();
            return CustomResult.fail("Unnstallation failed: " + null);
        }


    }


    @GetMapping("/chaosd/getStatusBySse")
    @OperationLog("获取Chaosd状态")
    public SseEmitter getChaosdStatusBySse(@RequestParam String agentId) {
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            Session session = null;
            try {
                // 根据 agentId 获取代理信息
                Nodeagent agent = nodeagentService.getById(agentId);

                // 创建 SSH session
                session = sshService.createSession(agent.getAgentIp(), agent.getAgentUsr(), agent.getAgentPwd());

                // 每隔5秒检查 Chaosd 的状态
                while (true) {
                    boolean isRunning = sshService.isChaosdRunning(session);
                    emitter.send(isRunning ? "Chaosd正在运行" : "Chaosd未运行", MediaType.TEXT_PLAIN);
                    Thread.sleep(5000); // 每5秒更新一次
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                // 关闭 session
                sshService.closeSession(session);
            }
        }).start();
        return emitter;
    }

    @GetMapping("/chaosd/getLogsBySse")
    @OperationLog("获取Chaosd日志")
    public SseEmitter getChaosdLogsBySse(@RequestParam String agentId) {
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            Session session = null;
            try {
                // 根据 agentId 获取代理信息
                Nodeagent agent = nodeagentService.getById(agentId);

                // 创建 SSH session
                session = sshService.createSession(agent.getAgentIp(), agent.getAgentUsr(), agent.getAgentPwd());

                // 从chaosd.log读取内容并发送
                String command = "tail -f /data/mj/chaosd/chaosd.log";
                sshService.executeCommand(session, command, line -> {
                    try {
                        emitter.send(line, MediaType.TEXT_PLAIN);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                // 关闭 session
                sshService.closeSession(session);
            }
        }).start();
        return emitter;
    }

    // 1. 新增 Nodeagent
    @PostMapping("/add")
    @OperationLog("新增节点代理")
    public CustomResult addNodeagent(@RequestBody Nodeagent Nodeagent) {
        Nodeagent.setIsDelete(0);
        boolean result = nodeagentService.save(Nodeagent);
        return result ? CustomResult.ok(Nodeagent.getId()) : CustomResult.fail("新增失败");
    }

    // 2. 根据ID删除 Nodeagent
    @DeleteMapping("/delete")
    @OperationLog("删除节点代理")
    public CustomResult deleteNodeagent(@RequestParam Long id) {
        Nodeagent Nodeagent = nodeagentService.getById(id);
        Nodeagent.setIsDelete(0);

        return CustomResult.ok();
    }

    // 3. 更新 Nodeagent 信息
    @PutMapping("/update")
    @OperationLog("更新节点代理")
    public CustomResult updateNodeagent(@RequestBody Nodeagent Nodeagent) {
        try {
            boolean result = nodeagentService.updateById(Nodeagent);
            return result ? CustomResult.ok() : CustomResult.fail("更新失败");
        } catch (Exception e) {
            // Log the exception and return a failure message with details
            e.printStackTrace(); // Or use a logger like log.error() for better logging
            return CustomResult.fail("更新失败，错误信息: " + null);
        }
    }

    // 4. 根据ID查询 Nodeagent 详情
    @GetMapping("/get")
    @OperationLog("获取节点代理详情")
    public CustomResult getNodeagentById(@RequestParam Long id) {
        Nodeagent Nodeagent = nodeagentService.getById(id);
        return Nodeagent != null ? CustomResult.ok(Nodeagent) : CustomResult.fail("未找到对应节点代理");
    }

    // 5. 分页查询 Nodeagent 列表
    @GetMapping("/list")
    @OperationLog("获取节点代理列表")
    public CustomResult listNodeagents(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Nodeagent> nodeagentPage = new Page<>(page, size);
        IPage<Nodeagent> resultPage = nodeagentService.page(nodeagentPage);

        // 将 Nodeagent 转换为 NodeagentDTO，但不查询状态
        List<NodeagentDTO> dtoList = resultPage.getRecords().stream()
                .map(nodeagent -> {
                    NodeagentDTO dto = convertToDTO(nodeagent);
                    // TODO: 状态查询可能导致长时间等待，暂时不查询状态
                    // 默认设置为未知状态(-1)
                    dto.setAgentStatus(0);
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 将转换后的列表和分页信息封装到自定义分页结果
        Page<NodeagentDTO> dtoPage = new Page<>(page, size);
        dtoPage.setRecords(dtoList);
        dtoPage.setTotal(resultPage.getTotal());
        dtoPage.setCurrent(resultPage.getCurrent());
        dtoPage.setSize(resultPage.getSize());

        return CustomResult.ok(dtoPage);
    }


    @GetMapping("/chaosd/getStatus")
    @OperationLog("获取Chaosd状态")
    public CustomResult getChaosdStatus(@RequestParam String agentId) {
        Session session = null;
        try {
            // 根据 agentId 获取代理信息
            Nodeagent agent = nodeagentService.getById(agentId);

            // 创建 SSH session
            session = sshService.createSession(agent.getAgentIp(), agent.getAgentUsr(), agent.getAgentPwd());

            // 检查 Chaosd 的状态
            boolean isRunning = sshService.isChaosdRunning(session);
            int status = isRunning ? 1 : 2; // 1: 运行中, 2: 未运行

            // 返回状态结果
            return CustomResult.ok(status);
        } catch (Exception e) {
            return CustomResult.fail("获取Chaosd状态失败", null);
        } finally {
            // 关闭 session
            sshService.closeSession(session);
        }
    }

    @GetMapping("/chaosd/getLogs")
    @OperationLog("获取Chaosd日志")
    public CustomResult getChaosdLogs(@RequestParam String agentId) {
        Session session = null;
        try {
            // 根据 agentId 获取代理信息
            Nodeagent agent = nodeagentService.getById(agentId);

            // 创建 SSH session
            session = sshService.createSession(agent.getAgentIp(), agent.getAgentUsr(), agent.getAgentPwd());

            // 获取最近100条日志记录
            String command = "tail -n 100 /data/mj/chaosd/chaosd.log";
            List<String> logs = sshService.executeCommand(session, command);

            // 返回日志记录
            return CustomResult.ok(logs);
        } catch (Exception e) {
            return CustomResult.fail("获取Chaosd日志失败", null);
        } finally {
            // 关闭 session
            sshService.closeSession(session);
        }
    }

    // 将 Nodeagent 转换为 NodeagentDTO
    private NodeagentDTO convertToDTO(Nodeagent nodeagent) {
        NodeagentDTO dto = new NodeagentDTO();
        dto.setId(nodeagent.getId());
        dto.setApplicationId(nodeagent.getApplicationId());
        dto.setAgentName(nodeagent.getAgentName());
        dto.setAgentIp(nodeagent.getAgentIp());
        dto.setAgentUsr(nodeagent.getAgentUsr());
        dto.setAgentPwd(nodeagent.getAgentPwd());
        dto.setCreateTime(nodeagent.getCreateTime());
        dto.setIsDelete(nodeagent.getIsDelete());
        dto.setUpdateTime(nodeagent.getUpdateTime());
        dto.setAgentPort(nodeagent.getAgentPort());
        return dto;
    }

    @GetMapping("/getNodeNameByIp")
    @OperationLog("根据IP获取节点名称")
    public CustomResult getNodeNameByIp(@RequestParam String nodeIp) {
        try {
            // 使用 nodeagentService 查询对应 IP 的节点代理
            Nodeagent nodeagent = nodeagentService.lambdaQuery()
                    .eq(Nodeagent::getAgentIp, nodeIp)
                    .eq(Nodeagent::getIsDelete, 0)  // 确保节点未被删除
                    .one();
            
            if (nodeagent != null) {
                return CustomResult.ok(nodeagent.getAgentName());
            } else {
                return CustomResult.fail("未找到对应IP的节点: " + nodeIp);
            }
        } catch (Exception e) {
            log.error("根据IP获取节点名称失败: {}", null, e);
            return CustomResult.fail("获取节点名称失败: " + null);
        }
    }

    /**
     * 获取简化版节点代理列表（不分页）
     * @return 节点代理简化列表
     */
    @GetMapping("/listSimple")
    @OperationLog("获取简化版节点代理列表")
    public CustomResult listSimpleNodeagents() {
        try {
            // 查询所有未删除的节点代理
            List<Nodeagent> nodeagents = nodeagentService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Nodeagent>()
                    .eq(Nodeagent::getIsDelete, 0)
            );
            
            // 转换为简化DTO
            List<Map<String, Object>> simpleList = nodeagents.stream()
                    .map(nodeagent -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", nodeagent.getId());
                        map.put("applicationId", nodeagent.getApplicationId());
                        map.put("agentName", nodeagent.getAgentName());
                        map.put("agentIp", nodeagent.getAgentIp());
                        map.put("agentPort", nodeagent.getAgentPort());
                        return map;
                    })
                    .collect(Collectors.toList());
            
            return CustomResult.ok(simpleList);
        } catch (Exception e) {
            log.error("获取简化节点代理列表失败", e);
            return CustomResult.fail("获取简化节点代理列表失败: " + null);
        }
    }
}

package com.iscas.lndicatormonitor.utils;

import com.iscas.lndicatormonitor.domain.Nodeagent;
import com.iscas.lndicatormonitor.service.NodeagentService;
import com.jcraft.jsch.*;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger; // 使用 Log4j 2
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Scanner;

@Component
public class NetworkInterfaceResolver {

    private static final Logger logger = LogManager.getLogger(NetworkInterfaceResolver.class);

    @Autowired
    private KubernetesClient kubernetesClient;

    @Autowired
    private NodeagentService nodeagentService;

    /**
     * 根据 namespace 和 podName 获取 Pod 的网络接口名称
     *
     * @param namespace 命名空间
     * @param podName   Pod 名称
     * @return 网络接口名称，如果未找到则返回 null
     */
    public String getPodNetworkInterface(String namespace, String podName) {
        try {
            logger.info("Starting to get network interface for Pod: {} in namespace: {}", podName, namespace);

            // Step 1: 获取 Pod 对象
            Pod pod = kubernetesClient.pods().inNamespace(namespace).withName(podName).get();
            if (pod == null) {
                logger.error("Pod not found: {} in namespace: {}", podName, namespace);
                return null;
            }
            logger.debug("Retrieved Pod: {}", pod.getMetadata().getName());

            // 获取节点名称和 Pod 的 IP 地址
            String nodeName = pod.getSpec().getNodeName();
            String podIp = pod.getStatus().getPodIP();
            logger.info("Pod {} is running on node: {}, IP: {}", podName, nodeName, podIp);

            // Step 2: 获取节点的账号和密码
            Nodeagent nodeAgent = nodeagentService.getNodeagentByName(nodeName);
            if (nodeAgent == null) {
                logger.error("Node agent not found for node: {}", nodeName);
                return null;
            }
            String username = nodeAgent.getAgentUsr();
            String password = nodeAgent.getAgentPwd();
            String host = nodeAgent.getAgentIp();
            logger.debug("Node agent info - Host: {}, Username: {}", host, username);

            // Step 3: SSH 登录到节点并获取网络接口
            String networkInterface = getNetworkInterfaceOverSSH(host, username, password, podIp);
            if (networkInterface != null) {
                logger.info("Successfully retrieved network interface: {} for Pod: {}", networkInterface, podName);
            } else {
                logger.warn("Failed to retrieve network interface for Pod: {}", podName);
            }
            return networkInterface;

        } catch (Exception e) {
            logger.error("Exception occurred while getting network interface for Pod: {}", podName, e);
            return null;
        }
    }

    /**
     * 使用 SSH 登录到节点，并根据 Pod 的 IP 获取对应的网络接口名称
     *
     * @param host     节点的 IP 地址
     * @param username SSH 登录用户名
     * @param password SSH 登录密码
     * @param podIp    Pod 的 IP 地址
     * @return 网络接口名称，如果未找到则返回 null
     */
    private String getNetworkInterfaceOverSSH(String host, String username, String password, String podIp) {
        Session session = null;
        try {
            logger.debug("Establishing SSH connection to host: {}", host);

            // 创建 JSch 对象
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            // 跳过 HostKey 检查（实际使用中应添加 HostKey 验证）
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(10000); // 10秒超时
            logger.debug("SSH session connected to host: {}", host);

            // 构建命令，查找 Pod IP 对应的网络接口
            String command = String.format("ip route | grep '%s'", podIp);
            logger.debug("Executing command to get network interface: {}", command);

            // 执行命令获取网络接口信息
            String commandOutput = executeCommand(session, command);
            if (commandOutput == null || commandOutput.isEmpty()) {
                logger.error("Failed to get network interface for Pod IP: {}", podIp);
                return null;
            }
            logger.debug("Command output: {}", commandOutput);

            // 解析网络接口名称
            String networkInterface = parseNetworkInterface(commandOutput);
            if (networkInterface != null) {
                logger.debug("Retrieved network interface: {}", networkInterface);
            } else {
                logger.warn("Network interface not found for Pod IP: {}", podIp);
            }

            return networkInterface;

        } catch (Exception e) {
            logger.error("Exception occurred during SSH operations to host: {}", host, e);
            return null;
        } finally {
            // 关闭连接
            if (session != null && session.isConnected()) {
                session.disconnect();
                logger.debug("SSH session disconnected");
            }
        }
    }

    /**
     * 执行 SSH 命令并返回结果
     *
     * @param session SSH 会话
     * @param command 要执行的命令
     * @return 命令输出结果
     * @throws Exception 执行过程中可能抛出的异常
     */
    private String executeCommand(Session session, String command) throws Exception {
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            InputStream in = channel.getInputStream();
            channel.connect();

            Scanner scanner = new Scanner(in).useDelimiter("\\A");
            String output = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
            return output.trim();
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
                logger.debug("SSH channel for command '{}' disconnected", command);
            }
        }
    }

    /**
     * 解析网络接口名称
     *
     * @param commandOutput 命令输出
     * @return 网络接口名称，如果未找到则返回 null
     */
    private String parseNetworkInterface(String commandOutput) {
        // 示例输出: "100.114.243.221 dev cali5c010fc0ee4 scope link"
        String[] lines = commandOutput.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.contains("dev")) {
                String[] parts = line.split("\\s+");
                for (int i = 0; i < parts.length; i++) {
                    if ("dev".equals(parts[i]) && i + 1 < parts.length) {
                        return parts[i + 1];
                    }
                }
            }
        }
        return null;
    }
}
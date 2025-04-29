package com.iscas.lndicatormonitor.service;
import com.iscas.lndicatormonitor.domain.Nodeagent;
import com.jcraft.jsch.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

@Service
public class SSHService {

    @Autowired
    NodeagentService nodeagentService;
    private static final Logger logger = LogManager.getLogger(SSHService.class);

    public Session createSession(String host, String user, String password) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, 22);
        session.setPassword(password);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        logger.info("正在连接到主机: {}", host);
        session.connect();
        logger.info("成功连接到主机: {}", host);
        return session;
    }

    public void closeSession(Session session) {
        if (session != null && session.isConnected()) {
            logger.info("正在断开与主机 {} 的会话。", session.getHost());
            session.disconnect();
        }
    }

    public void uploadAndInstall(String host, String user, String password, String remoteDir) throws Exception {
        JSch jsch = new JSch();
        Session session = null;

        try {
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            logger.info("正在连接到主机: {}", host);
            session.connect();
            logger.info("成功连接到主机: {}", host);
            String directoryPath = "/data/mj/chaosd"; // 目标目录


                String command = "mkdir -p " + directoryPath;
                executeCommand(session,command, false);  // 执行命令创建目录



            // 判断 Chaosd 是否在运行
            if (isChaosdRunning(session)) {
                logger.info("Chaosd 已经在运行，无需重新安装。");
                return;
            }

            // 判断 /usr/local/chaosd 是否存在
            if (remoteFileExists(session, "/usr/local/chaosd")) {
                logger.info("/usr/local/chaosd 存在，直接启动 Chaosd。");
                if (!remoteFileExists(session, remoteDir + "/install-directly.sh")){
                    logger.info("install-directly.sh 不存在，上传 install-directly.sh。");
                    uploadFile(session, "/data/mj/chaosd/install-directly.sh", remoteDir);
                }
                executeCommand(session, "chmod +x " + remoteDir + "/install-directly.sh && sh " + remoteDir + "/install-directly.sh",false);
                return;
            }

            // 判断 /data/mj/chaosd 是否有 chaosd-latest-linux-amd64 文件夹
            if (remoteFileExists(session, remoteDir + "/chaosd-latest-linux-amd64")) {
                logger.info("chaosd-latest-linux-amd64 目录存在，检查是否需要上传 install-without-tar.sh");
                if (!remoteFileExists(session, remoteDir + "/install-without-tar.sh")) {
                    uploadFile(session, "/data/mj/chaosd/install-without-tar.sh", remoteDir);
                }
                executeCommand(session, "chmod +x " + remoteDir + "/install-without-tar.sh && sh " + remoteDir + "/install-without-tar.sh",false);
                return;
            }

            // 判断是否存在 chaosd-latest-linux-amd64.tar.gz
            if (remoteFileExists(session, remoteDir + "/chaosd-latest-linux-amd64.tar.gz")) {
                logger.info("chaosd-latest-linux-amd64.tar.gz 存在，检查是否需要上传 install.sh");
                if (!remoteFileExists(session, remoteDir + "/install.sh")) {
                    uploadFile(session, "/data/mj/chaosd/install.sh", remoteDir);
                }
                executeCommand(session, "chmod +x " + remoteDir + "/install.sh && sh " + remoteDir + "/install.sh",false);
                return;
            }

            // 如果没有 chaosd-latest-linux-amd64.tar.gz，则上传并安装
            logger.info("chaosd-latest-linux-amd64.tar.gz 不存在，上传并执行安装。");
            uploadFile(session, "/data/mj/chaosd/chaosd-latest-linux-amd64.tar.gz", remoteDir);
            uploadFile(session, "/data/mj/chaosd/install.sh", remoteDir);
            executeCommand(session, "chmod +x " + remoteDir + "/install.sh " ,false);
            executeCommand(session, "sh " + remoteDir + "/install.sh",false);

            logger.info("Chaosd 在主机 {} 上安装完成。", host);
        } catch (Exception e) {
            logger.error("SSH 操作期间发生错误: ", e);
            throw e;
        } finally {
            if (session != null && session.isConnected()) {
                logger.info("正在断开与主机 {} 的会话。", host);
                session.disconnect();
            }
        }
    }

    public boolean isChaosdRunning(Session session) throws Exception {
        String command = "netstat -tuln | grep 31767";
        String result = executeCommandForResult(session, command);
        return result.contains("31767");
    }
    // 检查目录是否存在
    public static boolean directoryExists(String path) {
        return Files.exists(Paths.get(path)) && Files.isDirectory(Paths.get(path));
    }

    // 检查文件是否存在
    public static boolean fileExists(String path) {
        return Files.exists(Paths.get(path)) && Files.isRegularFile(Paths.get(path));
    }


    public boolean remoteFileExists(Session session, String filePath) throws Exception {
        String command = "if [ -e " + filePath + " ]; then echo 'exists'; fi";
        return executeCommandForResult(session, command).contains("exists");
    }

    public String executeCommandForResult(Session session, String command) throws Exception {
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        InputStream in = channelExec.getInputStream();
        channelExec.connect();

        byte[] tmp = new byte[1024];
        StringBuilder outputBuffer = new StringBuilder();
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                outputBuffer.append(new String(tmp, 0, i));
            }
            if (channelExec.isClosed()) {
                break;
            }
        }
        channelExec.disconnect();
        return outputBuffer.toString().trim();
    }

    public void uploadFile(Session session, String localFile, String remoteDir) throws Exception {
        ChannelSftp sftpChannel = null;
        try {
            // 打开 SFTP 通道
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            logger.info("正在连接 SFTP 通道...");
            sftpChannel.connect();
            logger.info("SFTP 连接成功，正在上传文件: {} 到远程目录: {}", localFile, remoteDir);

            // 上传文件
            sftpChannel.put(localFile, remoteDir);
            logger.info("文件上传成功: {} 到远程目录: {}", localFile, remoteDir);
        } catch (Exception e) {
            logger.error("文件上传过程中发生错误: {}", e.getMessage(), e);
            throw e; // 重新抛出异常以便上层处理
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
                logger.info("SFTP 通道已断开");
            }
        }
    }

    public void executeCommand(Session session, String command, Consumer<String> outputHandler) throws Exception {
        ChannelExec channelExec = null;
        try {
            logger.info("正在执行命令: {}", command);
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);
            channelExec.setErrStream(System.err);

            InputStream inputStream = channelExec.getInputStream();
            channelExec.connect();
            logger.info("命令执行开始，连接成功");

            // 使用 BufferedReader 按行读取输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("命令输出: {}", line); // 日志记录每行输出
                // 调用传递的回调函数，处理每一行输出
                outputHandler.accept(line);
            }

            logger.info("命令执行完成");

        } catch (Exception e) {
            logger.error("执行命令期间发生错误: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (channelExec != null) {
                channelExec.disconnect();
                logger.info("命令执行结束，连接已断开");
            }
        }
    }

    public void executeCommand(Session session, String command, boolean waitForOutput) throws Exception {
        ChannelExec channelExec = null;
        try {
            // 打开执行命令的通道
            logger.info("正在打开 exec 通道以执行命令...");
            channelExec = (ChannelExec) session.openChannel("exec");

            // 设置要执行的命令
            logger.info("设置执行命令: {}", command);
            channelExec.setCommand(command);
            channelExec.setErrStream(System.err);

            // 连接通道
            logger.info("正在连接通道以执行命令...");
            InputStream inputStream = channelExec.getInputStream();
            channelExec.connect();
            logger.info("命令通道连接成功，命令开始执行");

            if (waitForOutput) {
                // 如果需要等待输出，才执行读取输出的逻辑
                logger.info("开始读取命令输出...");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("命令输出: {}", line);
                }
                logger.info("命令输出读取完成");
            } else {
                // 如果不需要等待输出，直接返回
                logger.info("命令不需要等待输出，直接执行完毕");
            }

        } catch (Exception e) {
            logger.error("执行命令期间发生错误: {}", e.getMessage(), e);
            throw e;  // 抛出异常以供上层处理
        } finally {
            if (channelExec != null && channelExec.isConnected()) {
                // 断开通道
                logger.info("正在断开 exec 通道...");
                channelExec.disconnect();
                logger.info("exec 通道已断开");
            }
        }
    }
    public List<String> executeCommand(Session session, String command) throws Exception {
        List<String> result = new ArrayList<>();
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        InputStream inputStream = channel.getInputStream();
        channel.connect();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } finally {
            channel.disconnect();
        }

        return result;
    }
    public int getChaosdStatusById(Long agentId) {
        Session session = null;
        try {
            // 根据 agentId 获取代理信息
            Nodeagent agent = nodeagentService.getById(agentId);
            if (agent == null) {
                throw new RuntimeException("未找到该代理信息");
            }

            // 创建 SSH session
            session = createSession(agent.getAgentIp(), agent.getAgentUsr(), agent.getAgentPwd());

            // 检查 Chaosd 的状态
            boolean isRunning = isChaosdRunning(session);
            return isRunning ? 1 : 2; // 1: 运行中, 2: 未运行
        } catch (Exception e) {
            // 发生异常时返回状态 2（未运行）
            return 2;
        } finally {
            // 关闭 session
            if (session != null) {
                closeSession(session);
            }
        }
    }

}

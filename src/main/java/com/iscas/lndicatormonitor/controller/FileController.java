package com.iscas.lndicatormonitor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private MongoClient mongoClient;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${loadtestservicelist.loadScriptUrl}")
    private String loadScriptUrl;
    private static final Logger logger = LogManager.getLogger(FileController.class);
    
    // 定义文件类型白名单和黑名单
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jmx", "yaml", "yml", "json"
    ));
    
    private static final Set<String> BLOCKED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jsp", "asp", "aspx", "php", "exe", "bat", "sh", "dll", "jar", "class", "war", "ear"
    ));
    
    /**
     * 验证文件类型是否允许上传
     * @param fileName 文件名
     * @return 是否允许上传
     */
    private boolean isFileAllowed(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        
        // 获取文件扩展名
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        // 空扩展名不允许
        if (extension.isEmpty()) {
            logger.warn("文件没有扩展名: " + fileName);
            return false;
        }
        
        // 先检查黑名单
        if (BLOCKED_EXTENSIONS.contains(extension)) {
            logger.warn("文件类型在黑名单中: " + extension);
            return false;
        }
        
        // 再检查白名单
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            logger.warn("文件类型不在白名单中: " + extension);
            return false;
        }
        
        return true;
    }

    @PostMapping("/upload-yaml")
    @OperationLog("上传YAML文件")
    public CustomResult uploadYaml(@RequestParam("yamlFile") MultipartFile yamlFile) throws Exception {
        // 文件类型验证
        String originalFileName = yamlFile.getOriginalFilename();
        if (!isFileAllowed(originalFileName)) {
            logger.error("不允许上传该类型的文件: " + originalFileName);
            return CustomResult.fail("不允许上传该类型的文件，仅支持yaml/yml格式");
        }
        
        // 读取上传的YAML文件
        byte[] yamlBytes = yamlFile.getBytes();
        String yamlContent = new String(yamlBytes);

        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("microDefinition");

        // 将YAML文本内容直接插入MongoDB
        Document document = new Document("content", yamlContent);
        collection.insertOne(document);

        // 获取插入文档的ID
        ObjectId id = (ObjectId) document.get("_id");

        String _id = id.toHexString();
        return CustomResult.ok(_id);
    }

    @PostMapping("/upload-apifile")
    @OperationLog("上传API文档")
    public CustomResult uploadAPI(@RequestParam("jsonFile") MultipartFile jsonFile) throws Exception{
        // 文件类型验证
        String originalFileName = jsonFile.getOriginalFilename();
        if (!isFileAllowed(originalFileName)) {
            logger.error("不允许上传该类型的文件: " + originalFileName);
            return CustomResult.fail("不允许上传该类型的文件，仅支持json格式");
        }

        // 读取上传的JSON文件内容
        byte[] jsonBytes = jsonFile.getBytes();
        String jsonContent = new String(jsonBytes);

        // 连接到MongoDB
        MongoDatabase database = mongoClient.getDatabase("efficientTest");
        MongoCollection<Document> collection = database.getCollection("apiDoc");

        // 将JSON数据插入MongoDB
        Document document = new Document("content", jsonContent);
        collection.insertOne(document);

        // 获取插入文档的ID
        ObjectId id = (ObjectId) document.get("_id");

        String _id = id.toHexString();

        return CustomResult.ok(_id);
    }

    @PostMapping("/uploadJmx")
    @OperationLog("上传JMX文件")
    public CustomResult uploadJmxScript(@RequestParam("load_file") MultipartFile load_file) {
        logger.info("Received a file upload request");

        if (load_file.isEmpty()) {
            logger.warn("File upload failed: file is empty");
            return CustomResult.fail("文件为空");
        }
        
        // 文件类型验证
        String originalFileName = load_file.getOriginalFilename();
        if (!isFileAllowed(originalFileName)) {
            logger.error("不允许上传该类型的文件: " + originalFileName);
            return CustomResult.fail("不允许上传该类型的文件，仅支持jmx格式");
        }

        try {
            // 生成唯一文件名，避免文件名冲突
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            Path filePath = Paths.get(uploadDir + File.separator + uniqueFileName);

            logger.debug("Generated unique file name: " + uniqueFileName);

            // 确保上传目录存在
            Files.createDirectories(filePath.getParent());
            logger.debug("Upload directory ensured: " + filePath.getParent());

            // 保存文件到上传目录
            Files.write(filePath, load_file.getBytes());
            logger.info("File saved successfully at " + filePath);

            // 构造文件 URL（假设已知服务器基础 URL）
            String fileUrl = loadScriptUrl + uniqueFileName;
            logger.info("File URL generated: " + fileUrl);

            return CustomResult.ok(fileUrl);
        } catch (IOException e) {
            logger.error("File upload failed due to an IO exception", e);
            return CustomResult.fail("文件上传失败: " + null);
        } catch (Exception ex) {
            logger.error("Unexpected error occurred during file upload", ex);
            return CustomResult.fail("文件上传失败，发生意外错误: " + ex.getMessage());
        }
    }
}

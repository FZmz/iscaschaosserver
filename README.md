# 混沌工程监控平台后端服务

## 项目简介
这是一个基于Spring Boot的混沌工程监控平台后端服务，用于监控和管理混沌工程实验。该平台集成了Chaos Mesh进行故障注入，并通过Coroot进行系统监控。

## 环境要求
- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis
- Kubernetes 集群
- Chaos Mesh
- Coroot

## 前置条件
在部署前需要确认以下配置：

1. Prometheus配置
   - 确认Prometheus服务端口
   - 确认所在宿主机的编码类型

2. Kubernetes配置
   - 准备好k8s token（用于访问集群）
   - 确定被测平台所在命名空间
   - 提前建立好workflow所在的命名空间（默认为sock-shop）

3. Coroot配置
   - 确保Coroot服务正常运行
   - 配置正确的访问地址和认证信息

## 配置说明
主要配置文件位于`src/main/resources/application.yml`，需要配置以下内容：

```yaml
kubernetes:
  api-url: ${K8S_API_URL}
  token: ${K8S_TOKEN}
  verify-ssl: false

chaos:
  mesh:
    url: ${CHAOS_MESH_URL}
    token: ${CHAOS_MESH_TOKEN}

coroot:
  url: ${COROOT_URL}
  projectId: ${COROOT_PROJECT_ID}
  cookie: ${COROOT_COOKIE}
```

## 部署步骤
1. 克隆项目
```bash
git clone [项目地址]
```

2. 修改配置文件
   - 复制`application.yml.example`为`application.yml`
   - 填入相应的配置信息

3. 编译打包
```bash
mvn clean package
```

4. 运行服务
```bash
java -jar target/indicatormonitor.jar
```

## 主要功能
- 混沌实验管理
- 故障注入控制
- 系统监控数据采集
- 实验记录追踪
- 性能指标分析

## API文档
API文档通过Swagger提供，服务启动后访问：
```
http://[服务地址]:[端口]/swagger-ui.html
```

## 注意事项
1. 确保所有依赖服务（MySQL、Redis等）正常运行
2. 检查网络连通性，确保可以正常访问K8s集群
3. 定期检查和更新token等认证信息
4. 建议在生产环境使用HTTPS

## 问题排查
如遇到问题，请检查：
1. 日志文件
2. 网络连接
3. 配置文件
4. 认证信息

## 维护者
[维护者信息]

## 许可证
[许可证信息]
```

这个README包含了：
1. 项目简介和主要功能
2. 环境要求和前置条件
3. 详细的配置说明
4. 部署步骤
5. API文档位置
6. 问题排查指南

你可以根据实际情况进一步调整和补充内容。

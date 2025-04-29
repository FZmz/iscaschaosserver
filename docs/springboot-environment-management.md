# Spring Boot 环境配置管理总结

## 环境配置管理的基本方法

### 1. 配置文件命名规范
```
src/main/resources/
    ├── application.yml                # 主配置文件（公共配置）
    ├── application-dev.yml            # 开发环境
    ├── application-test.yml           # 测试环境
    ├── application-test-{name}.yml    # 特定测试环境
    └── application-prod.yml           # 生产环境
```

### 2. Maven Profile 配置
```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <profile.active>dev</profile.active>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    
    <profile>
        <id>test</id>
        <properties>
            <profile.active>test</profile.active>
        </properties>
    </profile>
    
    <profile>
        <id>prod</id>
        <properties>
            <profile.active>prod</profile.active>
        </properties>
    </profile>
</profiles>

<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```

### 3. 主配置文件设置
```yaml
spring:
  profiles:
    active: @profile.active@
```

## 环境切换方法

### 1. 打包时指定环境
```bash
mvn clean package -Pdev
mvn clean package -Ptest
mvn clean package -Pprod
```

### 2. 运行时指定环境
```bash
java -jar app.jar --spring.profiles.active=dev
java -jar app.jar --spring.profiles.active=test
java -jar app.jar --spring.profiles.active=prod
```

### 3. 容器化部署时指定环境
```yaml
# Docker
docker run -e "SPRING_PROFILES_ACTIVE=prod" your-image

# Kubernetes
env:
  - name: SPRING_PROFILES_ACTIVE
    value: "prod"
```

## 高级配置管理方案

### 1. 配置中心（推荐）
- Nacos
- Spring Cloud Config
- Apollo

```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: ${NACOS_SERVER:localhost:8848}
        namespace: ${NAMESPACE:dev}
        file-extension: yaml
```

### 2. 敏感信息处理
- 环境变量
- Kubernetes Secrets
- Vault

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:app}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:password}
```

### 3. 配置分层
- 基础配置（所有环境通用）
- 环境特定配置
- 应用特定配置
- 实例特定配置

## 最佳实践

1. **配置分离**：将配置与代码分离，便于管理和更新

2. **敏感信息保护**：不要在代码库中存储敏感信息

3. **版本控制**：对配置文件进行版本控制

4. **配置验证**：启动时验证配置的正确性

5. **文档化**：为每个环境的配置添加详细注释

6. **自动化部署**：使用CI/CD自动选择正确的环境配置

7. **监控配置**：记录配置变更，便于追踪问题

8. **配置热更新**：支持不重启应用更新配置

## 常见问题解决方案

1. **多环境配置冲突**：使用配置中心的命名空间隔离

2. **配置更新不生效**：检查缓存机制，确保正确刷新

3. **敏感信息泄露**：使用加密工具或密钥管理系统

4. **环境配置不一致**：使用配置中心统一管理

5. **配置过于复杂**：按功能模块拆分配置文件

## 多测试环境管理

对于多个测试环境的情况，可以采用以下策略：

1. **命名规范**：使用一致的命名模式，如 `application-test-{环境名}.yml`

2. **环境变量区分**：使用环境变量来区分不同的测试环境

3. **配置中心分组**：在配置中心中为每个测试环境创建单独的命名空间

4. **CI/CD 流水线**：为每个测试环境配置独立的部署流水线

5. **Docker Compose**：使用 Docker Compose 管理不同测试环境的容器组合

6. **Kubernetes 命名空间**：在 Kubernetes 中使用不同的命名空间隔离测试环境

## 总结

良好的环境配置管理可以显著提高开发效率、减少部署错误，并增强系统的可维护性。对于大型项目，强烈建议使用配置中心来统一管理所有环境的配置。通过合理的配置管理策略，可以使开发、测试和部署过程更加顺畅，减少环境差异带来的问题。 
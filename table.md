# 数据库表结构说明

## 基础信息表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| address | address | 物理机故障地址表 | id | id | 地址ID | INTEGER | 是 | | 是 |
| | | | address | address | 物理机故障chaosd地址 | VARCHAR | | | 是 |
| | | | fault_config_id | faultConfigId | 故障配置id | INTEGER | | 是 | 是 |
| application | application | 应用信息表 | id | id | 应用表id | VARCHAR | 是 | | 是 |
| | | | name | name | 应用名称 | VARCHAR | | | 是 |
| | | | description | description | 应用描述 | VARCHAR | | | |
| | | | creator | creator | 创建者 | VARCHAR | | | |
| | | | createTime | createtime | 创建时间 | TIMESTAMP | | | |
| | | | updateTime | updatetime | 更新时间 | TIMESTAMP | | | |
| | | | isDelete | isdelete | 是否删除 | INTEGER | | | |
| | | | namespace | namespace | k8s命名空间 | VARCHAR | | | |
| audit | audit | 审计日志表 | id | id | 审计id | INTEGER | 是 | | 是 |
| | | | userId | userid | 用户id | INTEGER | | 是 | 是 |
| | | | operate_time | operateTime | 操作时间 | TIMESTAMP | | | 是 |
| | | | operate_name | operateName | 操作名称 | VARCHAR | | | 是 |
| | | | operate_result | operateResult | 操作结果 | INTEGER | | | 是 |

## API与连接表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| apiDefinition | apiDefinition | API定义表 | id | id | api Id | VARCHAR | 是 | | 是 |
| | | | application_id | applicationId | 应用id | VARCHAR | | 是 | 是 |
| | | | name | name | api名称 | VARCHAR | | | 是 |
| | | | description | description | api描述 | VARCHAR | | | |
| | | | method | method | api方法 | VARCHAR | | | 是 |
| | | | url | url | api url | VARCHAR | | | 是 |
| | | | request_body | requestBody | 请求体 | VARCHAR | | | |
| | | | request_params | requestParams | 请求参数 | VARCHAR | | | |
| | | | request_headers | requestHeaders | 请求头 | VARCHAR | | | |
| | | | create_time | createTime | 创建时间 | TIMESTAMP | | | |
| | | | update_time | updateTime | 更新时间 | TIMESTAMP | | | |
| | | | is_delete | isDelete | 是否删除 | INTEGER | | | |
| connection | connection | 服务连接关系表 | id | id | 连接id | VARCHAR | 是 | | 是 |
| | | | application_id | applicationId | 应用id | VARCHAR | | 是 | 是 |
| | | | from_id | fromId | 来源id(服务) | VARCHAR | | 是 | 是 |
| | | | to_id | toId | 去向id(服务) | VARCHAR | | 是 | 是 |
| linkrequest | linkrequest | 链路请求表 | id | id | 链路请求id | INTEGER | 是 | | 是 |
| | | | task_id | taskId | 任务id | VARCHAR | | 是 | 是 |
| | | | content | content | 请求内容 | LONGVARCHAR | | | 是 |

## 故障配置表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| faultconfig | faultconfig | 故障配置表 | id | id | 演练故障id | INTEGER | 是 | | 是 |
| | | | name | name | 故障演练名称 | VARCHAR | | | 是 |
| | | | create_time | createTime | 创建时间 | TIMESTAMP | | | 是 |
| | | | update_time | updateTime | 更新时间 | TIMESTAMP | | | |
| | | | creator_id | creatorId | 创建人id | INTEGER | | 是 | 是 |
| | | | graph | graph | 故障配置流程图 | LONGVARCHAR | | | |
| | | | fault_type_config | faultTypeConfig | 故障类别配置 | LONGVARCHAR | | | |
| | | | node_tag | nodeTag | go端节点name | LONGVARCHAR | | | |
| faultcorrelation | faultcorrelation | 故障与计划关联表 | id | id | 关联id | INTEGER | 是 | | 是 |
| | | | plan_id | planId | 演练计划id | INTEGER | | 是 | 是 |
| | | | fault_config_id | faultConfigId | 故障演练id | INTEGER | | 是 | 是 |
| faultinnernode | faultinnernode | 故障流程节点表 | id | id | 流程节点id | INTEGER | 是 | | 是 |
| | | | fault_config_id | faultConfigId | 所属故障id | INTEGER | | 是 | 是 |
| | | | content | content | 流程节点内容 | LONGVARCHAR | | | 是 |
| | | | node_index | nodeIndex | 流程节点序号 | INTEGER | | | 是 |
| | | | node_status | nodeStatus | 流程节点状态 | INTEGER | | | 是 |
| | | | name | name | 节点名称 | VARCHAR | | | 是 |
| | | | node_type | nodeType | 节点类型 | VARCHAR | | | 是 |

## 版本2故障配置表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| config_target | config_target | 配置目标表 | id | id | 目标id | INTEGER | 是 | | 是 |
| | | | fault_config_id | faultConfigId | 故障配置id | INTEGER | | 是 | 是 |
| | | | content | content | 目标内容 | VARCHAR | | | 是 |
| fault_config_v2 | fault_config_v2 | 故障配置V2表 | id | id | 配置id | INTEGER | 是 | | 是 |
| | | | name | name | 配置名称 | VARCHAR | | | 是 |
| | | | creatorName | creatorName | 创建者名称 | VARCHAR | | | 是 |
| | | | createTime | createTime | 创建时间 | TIMESTAMP | | | 是 |
| | | | updateTime | updateTime | 更新时间 | TIMESTAMP | | | |
| | | | graph | graph | 流程图 | VARCHAR | | | |
| | | | content | content | 配置内容 | VARCHAR | | | |
| | | | nodeTag | nodeTag | 节点标签 | VARCHAR | | | |
| | | | targetType | targetType | 目标类型 | INTEGER | | | |
| fault_config_node | fault_config_node | 故障配置节点表 | id | id | 节点id | INTEGER | 是 | | 是 |
| | | | faultConfigId | faultConfigId | 故障配置id | INTEGER | | 是 | 是 |
| | | | name | name | 节点名称 | VARCHAR | | | 是 |
| | | | content | content | 节点内容 | VARCHAR | | | 是 |
| | | | nodeIndex | nodeIndex | 节点索引 | INTEGER | | | 是 |
| | | | nodeStatus | nodeStatus | 节点状态 | INTEGER | | | 是 |
| | | | nodeType | nodeType | 节点类型 | VARCHAR | | | 是 |

## 指标及场景表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| faults | faults | 故障类型表 | id | id | 故障类型id | INTEGER | 是 | | 是 |
| | | | fault_name | faultName | 故障名称 | VARCHAR | | | 是 |
| | | | platform | platform | 平台 | VARCHAR | | | 是 |
| recommended_metrics | recommended_metrics | 推荐指标表 | id | id | 指标id | INTEGER | 是 | | 是 |
| | | | metric_name | metricName | 推荐指标名称 | VARCHAR | | | 是 |
| | | | tool | tool | 处理器工具 | VARCHAR | | | 是 |
| | | | operation_path | operationPath | 操作路径 | VARCHAR | | | |
| | | | description | description | 描述 | VARCHAR | | | |
| fault_metric_mapping | fault_metric_mapping | 故障指标映射表 | id | id | 映射id | INTEGER | 是 | | 是 |
| | | | fault_id | faultId | 故障id | INTEGER | | 是 | 是 |
| | | | metric_id | metricId | 指标id | INTEGER | | 是 | 是 |
| fault_scenario | fault_scenario | 故障场景表 | id | id | 场景id | INTEGER | 是 | | 是 |
| | | | plan_id | planId | 计划id | INTEGER | | 是 | 是 |
| | | | category_id | categoryId | 分类id | INTEGER | | 是 | 是 |
| | | | name | name | 场景名称 | VARCHAR | | | 是 |
| | | | description | description | 场景描述 | VARCHAR | | | |
| | | | creator_name | creatorName | 创建者名称 | VARCHAR | | | 是 |
| | | | created_at | createdAt | 创建时间 | TIMESTAMP | | | 是 |
| obeservedindexv2 | obeservedindexv2 | 观测指标V2表 | id | id | 指标id | INTEGER | 是 | | 是 |
| | | | faultConfigId | faultConfigId | 故障配置id | INTEGER | | 是 | 是 |
| | | | metricId | metricId | 指标id | INTEGER | | 是 | 是 |

## 负载测试表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| load | load | 负载测试表 | id | id | 负载id | VARCHAR | 是 | | 是 |
| | | | application_id | applicationId | 应用id | VARCHAR | | 是 | 是 |
| | | | name | name | 负载名称 | VARCHAR | | | 是 |
| | | | type | type | 负载类型 | INTEGER | | | 是 |
| | | | creat_time | creatTime | 创建时间 | TIMESTAMP | | | |
| | | | update_time | updateTime | 更新时间 | TIMESTAMP | | | |
| | | | is_delete | isDelete | 是否删除 | INTEGER | | | |
| load_api | load_api | 负载API表 | id | id | 负载API id | VARCHAR | 是 | | 是 |
| | | | load_id | loadId | 负载id | VARCHAR | | 是 | 是 |
| | | | target_service | targetService | 目标服务 | VARCHAR | | | 是 |
| | | | user_count | userCount | 用户数量 | INTEGER | | | 是 |
| | | | load_pattern | loadPattern | 负载模式 | VARCHAR | | | 是 |
| | | | duration | duration | 持续时间 | VARCHAR | | | 是 |
| | | | request_url | requestUrl | 自定义url | VARCHAR | | | |
| | | | request_params | requestParams | 自定义参数 | VARCHAR | | | |
| | | | created_time | createdTime | 创建时间 | TIMESTAMP | | | |
| | | | update_time | updateTime | 更新时间 | TIMESTAMP | | | |
| | | | is_delete | isDelete | 是否删除 | INTEGER | | | |
| | | | http_method | httpMethod | http方法 | VARCHAR | | | 是 |
| | | | custom_headers | customHeaders | 请求头 | LONGVARCHAR | | | |
| | | | request_body | requestBody | 请求体 | LONGVARCHAR | | | |
| | | | ramp_up_time | rampUpTime | 爬升时间 | INTEGER | | | |
| load_script | load_script | 负载脚本表 | id | id | 负载脚本id | VARCHAR | 是 | | 是 |
| | | | load_id | loadId | 负载id | VARCHAR | | 是 | 是 |
| | | | load_script_url | loadScriptUrl | 负载脚本地址 | VARCHAR | | | 是 |
| | | | create_time | createTime | 创建时间 | TIMESTAMP | | | |
| | | | update_time | updateTime | 更新时间 | TIMESTAMP | | | |
| | | | is_delete | isDelete | 是否删除 | INTEGER | | | |
| load_task | load_task | 负载任务表 | id | id | 任务id | BIGINT | 是 | | 是 |
| | | | load_id | loadId | 负载id | VARCHAR | | 是 | 是 |
| | | | test_id | testId | 测试id | VARCHAR | | | 是 |
| | | | create_time | createTime | 创建时间 | TIMESTAMP | | | |
| | | | end_time | endTime | 结束时间 | TIMESTAMP | | | |
| | | | result_path | resultPath | 结果路径 | VARCHAR | | | |
| | | | is_deleted | isDeleted | 是否删除 | TINYINT | | | |
| load_correlation | load_correlation | 负载关联表 | id | id | 关联id | INTEGER | 是 | | 是 |
| | | | load_task_id | loadTaskId | 测试任务id | VARCHAR | | 是 | 是 |
| | | | chaos_exercise_record_id | chaosExerciseRecordId | 故障演练记录id | INTEGER | | 是 | 是 |

## 用户安全表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| historyPwd | historyPwd | 密码历史表 | id | id | 密码历史id | INTEGER | 是 | | 是 |
| | | | userId | userid | 用户ID | INTEGER | | 是 | 是 |
| | | | passwordHash | passwordhash | 密码哈希 | VARCHAR | | | 是 |
| | | | changeDate | changedate | 改变日期 | TIMESTAMP | | | 是 |
| loginattempt | loginattempt | 登录尝试表 | id | id | 登录尝试id | INTEGER | 是 | | 是 |
| | | | userId | userId | 用户id | INTEGER | | 是 | 是 |
| | | | login_attempts | loginAttempts | 错误登录次数 | INTEGER | | | 是 |
| | | | lock_until | lockUntil | 锁定期限 | TIMESTAMP | | | |

## 演练记录表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| chaos_exercise_records | chaos_exercise_records | 混沌演练记录表 | id | id | 记录id | INTEGER | 是 | | 是 |
| | | | created_time | createdTime | 创建时间 | TIMESTAMP | | | 是 |
| | | | start_time | startTime | 开始时间 | TIMESTAMP | | | |
| | | | end_time | endTime | 结束时间 | TIMESTAMP | | | |
| | | | record_status | recordStatus | 记录状态 | TINYINT | | | 是 |
| | | | player_name | playerName | 执行人名称 | VARCHAR | | | |
| | | | plan_id | planId | 计划id | INTEGER | | 是 | 是 |
| nodeagent | nodeagent | 节点代理表 | id | id | 代理id | VARCHAR | 是 | | 是 |
| | | | application_id | applicationId | 应用id | VARCHAR | | 是 | 是 |
| | | | agent_name | agentName | 代理名称 | VARCHAR | | | 是 |
| | | | agent_ip | agentIp | 代理ip | VARCHAR | | | 是 |
| | | | agent_usr | agentUsr | 代理用户名 | VARCHAR | | | 是 |
| | | | agent_pwd | agentPwd | 代理密码 | VARCHAR | | | 是 |
| | | | create_time | createTime | 创建时间 | TIMESTAMP | | | |
| | | | is_delete | isDelete | 是否删除 | INTEGER | | | |
| | | | update_time | updateTime | 更新时间 | TIMESTAMP | | | |
| | | | agent_port | agentPort | 代理端口 | INTEGER | | | 是 | 
<think>

## 观测指标表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| observed_index | observed_index | 观测指标表 | id | id | 系统指标id | INTEGER | 是 | | 是 |
| | | | name | name | 指标名 | VARCHAR | | | 是 |
| | | | type | type | 观测指标类型 | INTEGER | | | 是 |
| | | | fault_config_id | faultConfigId | 所属故障演练id | INTEGER | | 是 | 是 |
| observedcorrelation | observedcorrelation | 观测指标关联表 | id | id | 关联id | INTEGER | 是 | | 是 |
| | | | record_id | recordId | 记录id | INTEGER | | 是 | 是 |
| | | | observed_id | observedId | 观测指标id | INTEGER | | 是 | 是 |

## 计划V2表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| planv2 | planv2 | 演练计划V2表 | id | id | 计划id | INTEGER | 是 | | 是 |
| | | | name | name | 计划名称 | VARCHAR | | | 是 |
| | | | description | description | 计划描述 | VARCHAR | | | |
| | | | creator_name | creatorName | 创建者名称 | VARCHAR | | | 是 |
| | | | create_time | createTime | 创建时间 | TIMESTAMP | | | 是 |
| | | | update_time | updateTime | 更新时间 | TIMESTAMP | | | |
| | | | schedule | schedule | 执行计划 | VARCHAR | | | |
| | | | status | status | 计划状态 | INTEGER | | | 是 |

## 工作流表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| workflow | workflow | 工作流表 | id | id | 工作流id | INTEGER | 是 | | 是 |
| | | | name | name | 工作流名称 | VARCHAR | | | 是 |
| | | | creator_id | creatorId | 创建者id | INTEGER | | 是 | 是 |
| | | | create_time | createTime | 创建时间 | TIMESTAMP | | | 是 |
| | | | update_time | updateTime | 更新时间 | TIMESTAMP | | | |
| | | | content | content | 工作流内容 | TEXT | | | |
| | | | status | status | 工作流状态 | INTEGER | | | 是 |

## 用户表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| user | user | 用户表 | id | id | 用户id | INTEGER | 是 | | 是 |
| | | | username | username | 用户名 | VARCHAR | | | 是 |
| | | | password | password | 密码 | VARCHAR | | | 是 |
| | | | email | email | 邮箱 | VARCHAR | | | |
| | | | create_time | createTime | 创建时间 | TIMESTAMP | | | 是 |
| | | | last_login_time | lastLoginTime | 最后登录时间 | TIMESTAMP | | | |
| | | | status | status | 用户状态 | INTEGER | | | 是 |
| | | | role | role | 用户角色 | VARCHAR | | | 是 |

## 标签表

| 表代码 | 表名称 | 表说明 | 字段名 | 字段代码 | 注释 | 数据类型 | 主键 | 外键 | 非空 |
|-------|-------|-------|-------|---------|------|---------|------|------|------|
| scenario_tags | scenario_tags | 场景标签表 | id | id | 标签id | INTEGER | 是 | | 是 |
| | | | name | name | 标签名称 | VARCHAR | | | 是 |
| | | | description | description | 标签描述 | VARCHAR | | | |
| scenario_tags_correlation | scenario_tags_correlation | 场景标签关联表 | id | id | 关联id | INTEGER | 是 | | 是 |
| | | | scenario_id | scenarioId | 场景id | INTEGER | | 是 | 是 |
| | | | tags_id | tagsId | 标签id | INTEGER | | 是 | 是 |
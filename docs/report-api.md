# 演练报告接口文档

## 基础信息
- 基础路径: `/reportv2`
- 返回格式: JSON
- 状态码: 
  - 20000: 成功
  - 50000: 失败

## 接口列表

### 1. 新增报告
- **接口**: `/add`
- **方法**: POST
- **请求体**:
```json
{
    "name": "演练报告名称",
    "chaosRecordId": 1,
    "creator": "admin",
    "status": "0",
    "content": "报告内容"
}
```
- **响应**:
```json
{
    "status": 20000,
    "msg": "OK",
    "data": 1  // 返回报告ID
}
```

### 2. 查询报告列表
- **接口**: `/list`
- **方法**: POST
- **请求体**:
```json
{
    "name": "报告名称",
    "creator": "创建者",
    "status": "状态",
    "recordId": 1,
    "orderByTime": "DESC",
    "startTime": "2025-01-01 00:00:00",
    "endTime": "2025-12-31 23:59:59",
    "pageNum": 1,
    "pageSize": 10
}
```
- **响应**:
```json
{
    "status": 20000,
    "msg": "OK",
    "data": {
        "records": [
            {
                "id": 1,
                "name": "演练报告1",
                "chaosRecordId": 1,
                "creator": "admin",
                "creatorTime": "2025-01-01 12:00:00",
                "updateTime": "2025-01-01 12:00:00",
                "status": "0",
                "content": "报告内容"
            }
        ],
        "total": 100,
        "size": 10,
        "current": 1
    }
}
```

### 3. 获取报告详情
- **接口**: `/detail/{id}`
- **方法**: GET
- **参数**: 
  - id: 报告ID (路径参数)
- **响应**:
```json
{
    "status": 20000,
    "msg": "OK",
    "data": {
        "id": 1,
        "name": "演练报告",
        "chaosRecordId": 1,
        "creator": "admin",
        "creatorTime": "2025-01-01 12:00:00",
        "updateTime": "2025-01-01 12:00:00",
        "status": "0",
        "content": "报告内容"
    }
}
```

### 4. 更新报告
- **接口**: `/update`
- **方法**: PUT
- **请求体**:
```json
{
    "id": 1,
    "name": "更新后的报告名称",
    "content": "更新后的报告内容",
    "status": "1"
}
```
- **响应**:
```json
{
    "status": 20000,
    "msg": "success",
    "data": null
}
```

### 5. 删除报告
- **接口**: `/delete/{id}`
- **方法**: DELETE
- **参数**: 
  - id: 报告ID (路径参数)
- **响应**:
```json
{
    "status": 20000,
    "msg": "success",
    "data": null
}
```

## 数据字典

### 报告状态(status)
- 0: 成功
- 1: 失败

### 排序方式(orderByTime)
- ASC: 升序
- DESC: 降序

## 注意事项
1. 所有时间格式均为: `yyyy-MM-dd HH:mm:ss`
2. 分页参数默认值: pageNum=1, pageSize=10
3. 查询条件均支持模糊匹配
4. 创建和更新时间由系统自动维护 
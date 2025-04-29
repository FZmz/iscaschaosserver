package com.iscas.lndicatormonitor.common;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作名称字典，用于将方法名转换为中文描述
 */
@Component
public class OperationNameDictionary {
    
    private final Map<String, String> operationMap = new HashMap<>();
    
    @PostConstruct
    public void init() {
        // 用户管理相关
        operationMap.put("login", "用户登录");
        operationMap.put("logout", "用户登出");
        operationMap.put("register", "用户注册");
        operationMap.put("updatePassword", "修改密码");
        operationMap.put("getUserInfo", "获取用户信息");
        operationMap.put("updateUserInfo", "更新用户信息");
        operationMap.put("getAllUsers", "获取所有用户");
        operationMap.put("deleteUser", "删除用户");
        
        // 审计相关
        operationMap.put("getAll", "获取所有审计日志");
        operationMap.put("delAuditById", "删除审计日志");
        
        // 计划管理相关
        operationMap.put("listPlans", "查询计划列表");
        operationMap.put("getPlanById", "获取计划详情");
        operationMap.put("createPlan", "创建计划");
        operationMap.put("updatePlan", "更新计划");
        operationMap.put("deletePlan", "删除计划");
        operationMap.put("executePlan", "执行计划");
        operationMap.put("stopPlan", "停止计划");
        
        // 指标监控相关
        operationMap.put("getIndicators", "获取指标列表");
        operationMap.put("getIndicatorById", "获取指标详情");
        operationMap.put("createIndicator", "创建指标");
        operationMap.put("updateIndicator", "更新指标");
        operationMap.put("deleteIndicator", "删除指标");
        operationMap.put("monitorIndicator", "监控指标");
        
        // 系统管理相关
        operationMap.put("getSystemConfig", "获取系统配置");
        operationMap.put("updateSystemConfig", "更新系统配置");
        operationMap.put("getSystemStatus", "获取系统状态");
        operationMap.put("restartSystem", "重启系统");
        
        // 角色权限相关
        operationMap.put("getRoles", "获取角色列表");
        operationMap.put("createRole", "创建角色");
        operationMap.put("updateRole", "更新角色");
        operationMap.put("deleteRole", "删除角色");
        operationMap.put("assignRole", "分配角色");
        
        // 报表相关
        operationMap.put("generateReport", "生成报表");
        operationMap.put("exportReport", "导出报表");
        operationMap.put("getReportList", "获取报表列表");
        
        // 其他常用操作
        operationMap.put("save", "保存");
        operationMap.put("update", "更新");
        operationMap.put("delete", "删除");
        operationMap.put("query", "查询");
        operationMap.put("export", "导出");
        operationMap.put("import", "导入");
        operationMap.put("upload", "上传");
        operationMap.put("download", "下载");
        operationMap.put("enable", "启用");
        operationMap.put("disable", "禁用");
        operationMap.put("approve", "审批");
        operationMap.put("reject", "拒绝");
        operationMap.put("submit", "提交");
        operationMap.put("cancel", "取消");
        operationMap.put("confirm", "确认");
        
        // 添加更多映射...
    }
    
    /**
     * 获取方法名对应的中文描述
     * @param methodName 方法名
     * @return 中文描述，如果没有找到对应的描述，则返回原方法名
     */
    public String getOperationName(String methodName) {
        return operationMap.getOrDefault(methodName, methodName);
    }
    
    /**
     * 获取方法名对应的中文描述，包含控制器名称
     * @param controllerName 控制器名称
     * @param methodName 方法名
     * @return 中文描述，格式为"[控制器]-[操作]"
     */
    public String getOperationName(String controllerName, String methodName) {
        String operation = operationMap.getOrDefault(methodName, methodName);
        
        // 移除Controller后缀
        if (controllerName.endsWith("Controller")) {
            controllerName = controllerName.substring(0, controllerName.length() - 10);
        }
        
        return controllerName + "-" + operation;
    }
}
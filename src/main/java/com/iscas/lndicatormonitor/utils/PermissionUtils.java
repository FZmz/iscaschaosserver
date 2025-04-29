package com.iscas.lndicatormonitor.utils;

import com.iscas.lndicatormonitor.domain.Role;
import lombok.extern.slf4j.Slf4j;

/**
 * 权限管理工具类
 * 用户管理员拥有管理用户信息的权限
 * 审计员拥有查看审计信息的权限
 * 操作员拥有核心业务操作的权限
 * 超级管理员拥有所有权限
 */
@Slf4j
public class PermissionUtils {

    /**
     * 检查用户是否有权限访问用户管理功能
     * @param role 用户角色
     * @return 是否有权限
     */
    public static boolean canAccessUserManagement(Role role) {
        boolean hasPermission = role != null && (role.getRoleType() == Role.ROLE_ADMIN || role.getRoleType() == Role.ROLE_SUPER_ADMIN);
        log.debug("权限工具: 检查角色 {} 是否可以访问用户管理: {}", role, hasPermission);
        return hasPermission;
    }

    /**
     * 检查用户是否有权限访问审计日志功能
     * @param role 用户角色
     * @return 是否有权限
     */
    public static boolean canAccessAuditLog(Role role) {
        boolean hasPermission = role != null && (role.getRoleType() == Role.ROLE_AUDITOR || role.getRoleType() == Role.ROLE_SUPER_ADMIN);
        log.debug("权限工具: 检查角色 {} 是否可以访问审计日志: {}", role, hasPermission);
        return hasPermission;
    }

    /**
     * 检查用户是否有权限访问操作功能（除用户管理和审计日志外的所有功能）
     * @param role 用户角色
     * @return 是否有权限
     */
    public static boolean canAccessOperations(Role role) {
        boolean hasPermission = role != null && (role.getRoleType() == Role.ROLE_OPERATOR || role.getRoleType() == Role.ROLE_SUPER_ADMIN);
        log.debug("权限工具: 检查角色 {} 是否可以访问操作功能: {}", role, hasPermission);
        return hasPermission;
    }

    /**
     * 检查用户是否有权限访问特定功能
     * @param role 用户角色
     * @param functionType 功能类型（可以定义常量）
     * @return 是否有权限
     */
    public static boolean hasPermission(Role role, String functionType) {
        log.info("权限工具: 检查角色 {} 对功能 {} 的权限", role, functionType);
        
        if (role == null) {
            log.info("权限工具: 角色为空，拒绝访问");
            return false;
        }
        
        int roleType = role.getRoleType();
        
        // 超级管理员拥有所有权限
        if (roleType == Role.ROLE_SUPER_ADMIN) {
            log.info("权限工具: 超级管理员角色，允许访问所有功能");
            return true;
        }
        
        boolean hasPermission = false;
        
        switch (functionType) {
            // 用户管理相关功能 - 只有管理员可以访问
            case "USER_MANAGEMENT":
                hasPermission = roleType == Role.ROLE_ADMIN;
                log.info("权限工具: 检查用户管理权限: {}", hasPermission);
                break;
                
            // 审计日志相关功能 - 只有审计员可以访问
            case "AUDIT_LOG":
                hasPermission = roleType == Role.ROLE_AUDITOR;
                log.info("权限工具: 检查审计日志权限: {}", hasPermission);
                break;
                
            // 以下是核心业务功能 - 操作员和审计员都可以访问
                
            // 计划管理相关
            case "PLAN_MANAGEMENT":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查计划管理权限: {}", hasPermission);
                break;
                
            // 工作流管理相关
            case "WORKFLOW_MANAGEMENT":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查工作流管理权限: {}", hasPermission);
                break;
                
            // 故障管理相关
            case "FAULT_MANAGEMENT":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查故障管理权限: {}", hasPermission);
                break;
                
            // 节点管理相关
            case "NODE_MANAGEMENT":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查节点管理权限: {}", hasPermission);
                break;
                
            // 负载测试相关
            case "LOAD_TEST":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查负载测试权限: {}", hasPermission);
                break;
                
            // 应用管理相关
            case "APPLICATION_MANAGEMENT":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查应用管理权限: {}", hasPermission);
                break;
                
            // 指标管理相关
            case "METRICS_MANAGEMENT":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查指标管理权限: {}", hasPermission);
                break;
                
            // 实验观测相关
            case "EXPERIMENT_OBSERVATION":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查实验观测权限: {}", hasPermission);
                break;
                
            // 自动化测试相关
            case "AUTO_TEST":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查自动化测试权限: {}", hasPermission);
                break;
                
            // 链路追踪相关
            case "TRACE_DATA":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查链路追踪权限: {}", hasPermission);
                break;
                
            // Kubernetes操作相关
            case "K8S_OPERATIONS":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查Kubernetes操作权限: {}", hasPermission);
                break;
                
            // Chaos Mesh操作相关
            case "CHAOS_OPERATIONS":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查Chaos Mesh操作权限: {}", hasPermission);
                break;
                
            // AI运维相关
            case "AI_OPS":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查AI运维权限: {}", hasPermission);
                break;
                
            // 报告管理相关
            case "REPORT_MANAGEMENT":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查报告管理权限: {}", hasPermission);
                break;
                
            // 仪表盘相关
            case "DASHBOARD":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查仪表盘权限: {}", hasPermission);
                break;
                
            // 演练日志相关
            case "EXERCISE_LOGS":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查演练日志权限: {}", hasPermission);
                break;
                
            // 系统配置相关
            case "SYSTEM_CONFIGURATION":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查系统配置权限: {}", hasPermission);
                break;
            
            // 服务管理相关
            case "SERVICE_MANAGEMENT":
                hasPermission = roleType == Role.ROLE_OPERATOR;
                log.info("权限工具: 检查服务管理权限: {}", hasPermission);
                break;
                
            default:
                // 未定义的功能类型，默认拒绝访问
                log.info("权限工具: 未知功能类型: {}，拒绝访问", functionType);
                hasPermission = false;
                break;
        }
        
        log.info("权限工具: 角色 {} 对功能 {} 的权限检查结果: {}", 
            roleType, functionType, hasPermission);
        return hasPermission;
    }
} 
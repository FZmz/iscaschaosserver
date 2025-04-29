package com.iscas.lndicatormonitor.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Role;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.service.RoleService;
import com.iscas.lndicatormonitor.service.UsersService;
import com.iscas.lndicatormonitor.utils.JwtTokenUtil;
import com.iscas.lndicatormonitor.utils.PermissionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

    @Autowired
    private UsersService usersService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 定义URL和功能类型的映射
    private static final Map<String, String> URL_FUNCTION_MAP = new HashMap<>();

    static {
        // 用户管理
        URL_FUNCTION_MAP.put("/user", "USER_MANAGEMENT");

        // 审计日志
        URL_FUNCTION_MAP.put("/audit", "AUDIT_LOG");
        URL_FUNCTION_MAP.put("/operate", "AUDIT_LOG");

        // 计划管理
        URL_FUNCTION_MAP.put("/plan", "PLAN_MANAGEMENT");
        URL_FUNCTION_MAP.put("/planv2", "PLAN_MANAGEMENT");

        // 工作流管理
        URL_FUNCTION_MAP.put("/workflow", "WORKFLOW_MANAGEMENT");

        // 故障管理
        URL_FUNCTION_MAP.put("/fault", "FAULT_MANAGEMENT");
        URL_FUNCTION_MAP.put("/faultConfig", "FAULT_MANAGEMENT");
        URL_FUNCTION_MAP.put("/faultConfigv2", "FAULT_MANAGEMENT");
        URL_FUNCTION_MAP.put("/scenarioCategory", "FAULT_MANAGEMENT");
        URL_FUNCTION_MAP.put("/faultScenario", "FAULT_MANAGEMENT");
        URL_FUNCTION_MAP.put("/scenarioTag", "FAULT_MANAGEMENT");

        // 报告管理
        URL_FUNCTION_MAP.put("/report", "REPORT_MANAGEMENT");
        URL_FUNCTION_MAP.put("/reportv2", "REPORT_MANAGEMENT");

        // 节点管理
        URL_FUNCTION_MAP.put("/nodeagent", "NODE_MANAGEMENT");
        URL_FUNCTION_MAP.put("/nodeMetrics", "NODE_MANAGEMENT");

        // 负载测试
        URL_FUNCTION_MAP.put("/load", "LOAD_TEST");
        URL_FUNCTION_MAP.put("/file", "LOAD_TEST");

        // 仪表盘
        URL_FUNCTION_MAP.put("/dashboard", "DASHBOARD");
        URL_FUNCTION_MAP.put("/bigScreen", "DASHBOARD");

        // 应用管理
        URL_FUNCTION_MAP.put("/application", "APPLICATION_MANAGEMENT");
        URL_FUNCTION_MAP.put("/apidefinition", "APPLICATION_MANAGEMENT");

        // 观测指标
        URL_FUNCTION_MAP.put("/observedIndex", "METRICS_MANAGEMENT");
        URL_FUNCTION_MAP.put("/indicator", "METRICS_MANAGEMENT");

        // 演练日志
        URL_FUNCTION_MAP.put("/logs", "EXERCISE_LOGS");

        // 实验观测
        URL_FUNCTION_MAP.put("/experimentObservation", "EXPERIMENT_OBSERVATION");

        // 自动化测试
        URL_FUNCTION_MAP.put("/autoTest", "AUTO_TEST");

        // 链路追踪
        URL_FUNCTION_MAP.put("/rcadata", "TRACE_DATA");

        // Kubernetes操作
        URL_FUNCTION_MAP.put("/k8s", "K8S_OPERATIONS");

        // Chaos Mesh操作
        URL_FUNCTION_MAP.put("/chaos", "CHAOS_OPERATIONS");

        // AI运维
        URL_FUNCTION_MAP.put("/aiops", "AI_OPS");

        // 以下是补充的映射

        // 节点代理
        URL_FUNCTION_MAP.put("/nodeagent", "NODE_MANAGEMENT");

        // 实验记录
        URL_FUNCTION_MAP.put("/record", "EXERCISE_LOGS");

        // 稳态管理
        URL_FUNCTION_MAP.put("/steady", "METRICS_MANAGEMENT");

        // 地址管理
        URL_FUNCTION_MAP.put("/address", "SYSTEM_CONFIGURATION");

        // 系统配置
        URL_FUNCTION_MAP.put("/config", "SYSTEM_CONFIGURATION");

        // 角色管理
        URL_FUNCTION_MAP.put("/role", "USER_MANAGEMENT");

        // 密码历史
        URL_FUNCTION_MAP.put("/historyPwd", "USER_MANAGEMENT");

        // 登录尝试
        URL_FUNCTION_MAP.put("/loginattempt", "USER_MANAGEMENT");

        // 通知管理
        URL_FUNCTION_MAP.put("/notification", "SYSTEM_CONFIGURATION");

        // 状态边界
        URL_FUNCTION_MAP.put("/statebound", "METRICS_MANAGEMENT");

        // 故障关联
        URL_FUNCTION_MAP.put("/faultcorrelation", "FAULT_MANAGEMENT");

        // 故障内部节点
        URL_FUNCTION_MAP.put("/faultinnernode", "FAULT_MANAGEMENT");

        // 选择器
        URL_FUNCTION_MAP.put("/selector", "FAULT_MANAGEMENT");

        // 故障指标映射
        URL_FUNCTION_MAP.put("/faultMetricMapping", "FAULT_MANAGEMENT");

        // 推荐指标
        URL_FUNCTION_MAP.put("/recommendedMetrics", "METRICS_MANAGEMENT");

        // 故障类型
        URL_FUNCTION_MAP.put("/faults", "FAULT_MANAGEMENT");

        // 记录管理
        URL_FUNCTION_MAP.put("/recordv2", "EXERCISE_LOGS");

        // 指标管理
        URL_FUNCTION_MAP.put("/metrics", "METRICS_MANAGEMENT");

        // 用户相关
        URL_FUNCTION_MAP.put("/login", "USER_MANAGEMENT");
        URL_FUNCTION_MAP.put("/logout", "USER_MANAGEMENT");

        // 服务管理
        URL_FUNCTION_MAP.put("/service", "SERVICE_MANAGEMENT");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestURI = request.getRequestURI();
        logger.info("=== 权限拦截器开始 ===");
        logger.info("权限拦截器处理请求: {} {}", request.getMethod(), requestURI);

        // 登录、注销、验证码接口不需要权限检查
        if (requestURI.contains("/user/logout") ||
                requestURI.contains("/user/generateCaptcha") ||
                requestURI.contains("/user/verifyCaptcha")) {
            logger.info("权限拦截器: 跳过登录/注销/验证码端点的权限检查: {}", requestURI);
            return true;
        }
        System.out.println("requestURI: " + requestURI);
        System.out.println("if: " + requestURI.contains("/user/generateCaptcha"));
        // 获取请求头中的 token
        String token = request.getHeader("Authorization");
        logger.debug("权限拦截器: 授权头信息: {}", token);

        if (token == null || !token.startsWith("Bearer ")) {
            logger.warn("权限拦截器: 未找到有效的授权头");
            handleUnauthorized(response, "未提供有效的认证令牌");
            return false;
        }

        // 提取 token
        token = token.substring(7);
        logger.debug("权限拦截器: 提取的JWT令牌: {}", token);

        try {
            // 验证 token
            boolean isTokenValid = jwtTokenUtil.validateToken(token);
            logger.info("权限拦截器: 令牌验证结果: {}", isTokenValid);

            if (!isTokenValid) {
                logger.warn("权限拦截器: 无效的令牌");
                handleUnauthorized(response, "无效的认证令牌");
                return false;
            }

            // 从 token 中获取用户名
            String username = jwtTokenUtil.getUsernameFromToken(token);
            logger.info("权限拦截器: 从令牌中提取的用户名: {}", username);

            // 获取用户信息
            Users user = usersService.selectByUserName(username);
            logger.debug("权限拦截器: 找到用户: {}", user);

            if (user == null) {
                logger.warn("权限拦截器: 未找到用户: {}", username);
                handleUnauthorized(response, "用户不存在");
                return false;
            }

            Integer userId = user.getId();
            logger.debug("权限拦截器: 用户ID: {}", userId);

            // 修改密码接口允许所有已认证用户访问
            if (requestURI.contains("/user/updatePwd")) {
                logger.info("权限拦截器: 允许已认证用户访问密码更新端点: {}", username);
                return true;
            }

            // 获取用户角色
            Role role = roleService.selectByUserId(userId);
            logger.info("权限拦截器: 用户角色: {}", role);

            // 如果用户没有角色，检查是否是登录后的第一次请求
            if (role == null) {
                // 如果是用户管理相关的请求，拒绝访问
                if (requestURI.contains("/user/")) {
                    logger.warn("权限拦截器: 用户没有角色，拒绝访问用户管理端点: {}", requestURI);
                    handleUnauthorized(response, "用户角色不存在，请联系管理员分配角色");
                    return false;
                }

                // 对于其他请求，允许访问基本功能
                logger.warn("权限拦截器: 用户 {} 没有角色，允许访问基本功能", username);
                return true;
            }

            // 如果是新增用户接口，检查是否是管理员或超级管理员
            if (requestURI.contains("/user/add")) {
                boolean isAdmin = (role.getRoleType() == Role.ROLE_ADMIN
                        || role.getRoleType() == Role.ROLE_SUPER_ADMIN);
                logger.info("权限拦截器: 用户尝试访问用户添加端点，是否管理员: {}", isAdmin);

                if (!isAdmin) {
                    logger.warn("权限拦截器: 非管理员用户尝试添加用户: {}", username);
                    handleForbidden(response, "只有管理员或超级管理员才能新增用户");
                    return false;
                }

                logger.info("权限拦截器: 管理员用户允许添加用户: {}", username);
                return true;
            }

            // 获取功能类型
            String functionType = getFunctionType(requestURI);
            logger.info("权限拦截器: URI {} 的功能类型: {}", requestURI, functionType);

            if (functionType == null) {
                logger.info("权限拦截器: 未为URI定义功能类型: {}，允许访问", requestURI);
                return true; // 如果没有定义功能类型，默认允许访问
            }

            // 检查用户是否有权限访问该功能
            boolean hasPermission = PermissionUtils.hasPermission(role, functionType);
            logger.info("权限拦截器: 用户: {}，角色: {}，功能: {}，是否有权限: {}",
                    username, role.getRoleType(), functionType, hasPermission);

            if (!hasPermission) {
                logger.warn("权限拦截器: 用户 {} 角色 {} 没有权限访问功能 {}",
                        username, role.getRoleType(), functionType);
                handleForbidden(response, "无权限访问该功能");
                return false;
            }

            logger.info("权限拦截器: 用户 {} 角色 {} 有权限访问功能 {}",
                    username, role.getRoleType(), functionType);
            logger.info("=== 权限拦截器结束 ===");
            return true;
        } catch (Exception e) {
            logger.error("权限拦截器: 权限验证失败", e);
            handleUnauthorized(response, "权限验证失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 根据请求URI获取功能类型
     */
    private String getFunctionType(String uri) {
        logger.info("获取URI的功能类型: {}", uri);

        // 移除可能的前缀，如 /api
        String normalizedUri = uri;
        if (uri.startsWith("/api")) {
            normalizedUri = uri.substring(4); // 移除 "/api" 前缀
            logger.info("规范化URI: {} -> {}", uri, normalizedUri);
        }

        for (Map.Entry<String, String> entry : URL_FUNCTION_MAP.entrySet()) {
            String key = entry.getKey();
            // 如果key以/api开头，移除前缀
            if (key.startsWith("/api")) {
                key = key.substring(4);
                logger.info("规范化key: {} -> {}", entry.getKey(), key);
            }

            if (normalizedUri.contains(key)) {
                logger.info("找到URI的功能类型: {} 对应 {}, 匹配key: {}", entry.getValue(), uri, key);
                return entry.getValue();
            }
        }

        logger.info("未找到URI的功能类型: {}", uri);
        return null;
    }

    /**
     * 处理未授权的请求
     */
    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(new CustomResult(40100, message, null)));
    }

    /**
     * 处理无权限的请求
     */
    private void handleForbidden(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(objectMapper.writeValueAsString(new CustomResult(40000, message, null)));
    }
}
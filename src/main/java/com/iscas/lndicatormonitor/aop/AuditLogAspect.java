package com.iscas.lndicatormonitor.aop;

import com.iscas.lndicatormonitor.annotation.OperationLog;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.common.OperationNameDictionary;
import com.iscas.lndicatormonitor.domain.Audit;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.dto.UsersDTO;
import com.iscas.lndicatormonitor.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class AuditLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogAspect.class);
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private OperationNameDictionary operationNameDictionary;

    // 定义一个切点，匹配所有带有 @OperationLog 注解的方法
    @Pointcut("@annotation(com.iscas.lndicatormonitor.annotation.OperationLog)")
    public void operationLogMethods() {
        // 方法体为空，作为切点的标识
    }

    // 定义一个切点，匹配所有控制器方法
    @Pointcut("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {
        // 方法体为空，作为切点的标识
    }

    // 组合切点，匹配所有带有 @OperationLog 注解的控制器方法
    @Pointcut("operationLogMethods() && controllerMethods()")
    public void operationLogControllerMethods() {
        // 方法体为空，作为切点的标识
    }

    // 在方法执行之前
    @Before("operationLogControllerMethods()")
    public void logBeforeControllerCall(JoinPoint joinPoint) {
        logger.debug("准备执行带有 @OperationLog 注解的控制器方法: {}", joinPoint.getSignature().toShortString());
    }

    // 在方法返回后执行
    @AfterReturning(pointcut = "operationLogControllerMethods()", returning = "result")
    public void logAfterControllerCall(JoinPoint joinPoint, Object result) {
        try {
            logger.debug("开始记录审计日志...");
            
            // 获取方法签名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            
            // 获取控制器类名
            String controllerName = joinPoint.getTarget().getClass().getSimpleName();
            logger.debug("控制器名称: {}", controllerName);
            
            // 检查是否有OperationLog注解
            OperationLog operationLog = method.getAnnotation(OperationLog.class);
            
            // 如果没有注解或者不需要记录日志，则直接返回
            if (operationLog == null) {
                logger.debug("方法没有 @OperationLog 注解，使用字典作为备选方案");
                // 使用字典作为备选方案
                String methodName = method.getName();
                String operationName = operationNameDictionary.getOperationName(controllerName, methodName);
                logger.debug("从字典获取的操作名称: {}", operationName);
                recordAuditLog(operationName, result);
                return;
            }
            
            // 获取操作名称
            String operationName = operationLog.value();
            logger.debug("从注解获取的操作名称: {}", operationName);
            
            if (operationName.isEmpty()) {
                // 如果注解中没有指定操作名称，则使用字典
                String methodName = method.getName();
                operationName = operationNameDictionary.getOperationName(controllerName, methodName);
                logger.debug("注解中没有指定操作名称，从字典获取: {}", operationName);
            }
            
            // 记录审计日志
            recordAuditLog(operationName, result);
            
            // 如果需要记录请求参数
            if (operationLog.recordParams()) {
                Object[] args = joinPoint.getArgs();
                logger.debug("请求参数: {}", args);
                // 这里可以将参数保存到审计日志中
            }
            
            // 如果需要记录返回结果
            if (operationLog.recordResult()) {
                logger.debug("返回结果: {}", result);
                // 这里可以将返回结果保存到审计日志中
            }
            
            logger.debug("审计日志记录完成");
        } catch (Exception e) {
            logger.error("记录审计日志时发生异常", e);
        }
    }
    
    /**
     * 记录审计日志
     * @param operationName 操作名称
     * @param result 操作结果
     */
    private void recordAuditLog(String operationName, Object result) {
        logger.debug("开始记录审计日志，操作名称: {}", operationName);
        
        CustomResult customResult = null;
        if (result instanceof CustomResult) {
            customResult = (CustomResult) result;
            logger.debug("返回结果是 CustomResult 类型，状态码: {}", customResult.getStatus());
        } else {
            // 如果返回结果不是CustomResult类型，则不记录日志
            logger.debug("返回结果不是 CustomResult 类型，不记录日志");
            return;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("获取到认证信息: {}", authentication);
        
        if (authentication == null) {
            logger.warn("认证信息为空，不记录审计日志");
            return;
        }
        
        // 尝试获取用户名
        String username = null;
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UsersDTO) {
            UsersDTO userDetails = (UsersDTO) principal;
            username = userDetails.getUsername();
            System.out.println("username: " + username);
            logger.debug("从 UsersDTO 获取到用户名: {}", username);
        } else if (principal instanceof String) {
            username = (String) principal;
            logger.debug("从 String 获取到用户名: {}", username);
        } else if (principal instanceof  Users) {
            // 尝试从其他类型的 principal 中获取用户名
            Users user = (Users) principal;
            username = user.getRealName();
            logger.debug("从其他类型 ({}) 获取到用户名: {}", principal.getClass().getName(), username);
        }
        
        if (username == null || username.isEmpty() || "anonymousUser".equals(username)) {
            logger.warn("无法获取有效的用户名，不记录审计日志");
            return;
        }
        
        // 创建审计日志对象
        Audit audit = new Audit();
        audit.setOperateName(operationName);
        audit.setUsername(username);
        audit.setOperateTime(new Date());
        audit.setOperateResult(customResult.getStatus() == 40000 ? 2 : 1);
        
        // 保存审计日志
        try {
            auditService.insert(audit);
            logger.debug("已记录审计日志: 用户={}, 操作={}, 结果={}", 
                    username, operationName, 
                    customResult.getStatus() == 40000 ? "失败" : "成功");
        } catch (Exception e) {
            logger.error("保存审计日志失败", e);
        }
    }
}

package com.iscas.lndicatormonitor.filter;

import com.iscas.lndicatormonitor.domain.Role;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.service.RoleService;
import com.iscas.lndicatormonitor.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Order(3) // 确保在JwtRequestFilter之后执行
public class AuthorizationDebugFilter extends OncePerRequestFilter {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RoleService roleService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/api/user/generateCaptcha") || 
               path.contains("/api/user/verifyCaptcha") || 
               path.contains("/api/user/loginnew");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        log.info("=== 授权调试过滤器开始 ===");
        log.info("请求URI: {}", requestURI);
        
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("当前认证信息: {}", authentication);
        
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            log.info("已认证用户: {}", username);
            
            // 获取用户信息
            Users user;
            try {
                user = usersService.selectByUserName(username);
                if (user != null) {
                    log.info("用户ID: {}", user.getId());
                    
                    // 获取用户角色
                    Role role = roleService.selectByUserId(user.getId());
                    if (role != null) {
                        log.info("用户角色: {}, 角色类型: {}", role, role.getRoleType());
                        
                        // 检查是否是审计员
                        if (role.getRoleType() == Role.ROLE_AUDITOR) {
                            log.info("用户是审计员，检查请求路径是否会被拦截器处理");
                            
                            // 检查请求路径是否包含特定前缀
                            if (requestURI.startsWith("/api/")) {
                                log.info("请求路径以/api/开头，应该会被PermissionInterceptor处理");
                            } else {
                                log.info("请求路径不以/api/开头，可能不会被PermissionInterceptor处理");
                            }
                        }
                    } else {
                        log.warn("用户没有角色信息");
                    }
                } else {
                    log.warn("找不到用户信息");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        } else {
            log.info("用户未认证");
        }
        
        log.info("=== 授权调试过滤器结束 ===");
        chain.doFilter(request, response);
    }
}
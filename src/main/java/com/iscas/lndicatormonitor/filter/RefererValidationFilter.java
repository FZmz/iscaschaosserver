package com.iscas.lndicatormonitor.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class RefererValidationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RefererValidationFilter.class);

    // 修改为完整的URL格式
    private static final List<String> ALLOWED_DOMAINS = Arrays.asList(
            "http://10.147.245.41:8001",
            "http://116.63.51.45:8001",
            "http://localhost:8001",
            "http://localhost:5173",
            "http://127.0.0.1:8001",
            "http://127.0.0.1:5173"
    );

    // 白名单路径
    private static final List<String> WHITELIST_PATHS = Arrays.asList(
            // "/api/user/login",
            "/api/user/loginnew",
            "/api/user/generateCaptcha",
            "/api/user/verifyCaptcha",
            "/api/user/logout"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/api/user/generateCaptcha") || 
               path.contains("/api/user/verifyCaptcha") || 
            //    path.contains("/api/user/login") ||
               path.contains("/api/user/loginnew");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        logRequestDetails(request);
        
        String referer = request.getHeader("Referer");
        String origin = request.getHeader("Origin");
        
        // 1. 如果是OPTIONS请求，直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 2. 如果是本地请求，直接放行
        String serverName = request.getServerName();
        if ("localhost".equals(serverName) || "127.0.0.1".equals(serverName)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 3. 如果有身份认证信息，也直接放行
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 4. 检查Referer
        if (referer != null) {
            // 更简单直接的检查方式
            boolean isAllowed = ALLOWED_DOMAINS.stream()
                    .anyMatch(referer::startsWith);
                    
            if (isAllowed) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        
        // 5. 检查Origin
        if (origin != null) {
            boolean isAllowed = ALLOWED_DOMAINS.stream()
                    .anyMatch(origin::equals);
                    
            if (isAllowed) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        
        // 如果以上条件都不满足，记录警告并禁止访问
        logger.warn("Invalid Referer/Origin - Referer: {}, Origin: {}, IP: {}", 
                referer, origin, request.getRemoteAddr());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Invalid request source");
    }

    private void logRequestDetails(HttpServletRequest request) {
        logger.info("=== Request Details ===");
        logger.info("URI: {}", request.getRequestURI());
        logger.info("Method: {}", request.getMethod());
        logger.info("Server Name: {}", request.getServerName());
        logger.info("Referer: {}", request.getHeader("Referer"));
        logger.info("Origin: {}", request.getHeader("Origin"));
        logger.info("Authorization: {}", request.getHeader("Authorization") != null ? "Present" : "Not Present");
        logger.info("Remote Addr: {}", request.getRemoteAddr());
        logger.info("=====================");
    }
}
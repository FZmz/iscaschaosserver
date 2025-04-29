package com.iscas.lndicatormonitor.filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Order(1) // 确保这个过滤器最先执行
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        log.info("=== REQUEST START ===");
        log.info("Request received: {} {}", method, requestURI);
        log.info("Remote address: {}", request.getRemoteAddr());
        
        // 记录请求头
        Map<String, String> headers = getRequestHeaders(request);
        log.info("Request headers: {}", headers);
        
        // 记录请求参数
        Map<String, String[]> parameters = request.getParameterMap();
        if (!parameters.isEmpty()) {
            log.info("Request parameters: {}", parameters);
        }
        
        try {
            // 继续过滤器链
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            
            log.info("Response status: {}", status);
            log.info("Request duration: {} ms", duration);
            log.info("=== REQUEST END ===");
        }
    }
    
    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                
                // 敏感信息处理
                if ("Authorization".equalsIgnoreCase(headerName) && headerValue != null) {
                    if (headerValue.startsWith("Bearer ")) {
                        headerValue = "Bearer [REDACTED]";
                    }
                }
                
                headers.put(headerName, headerValue);
            }
        }
        
        return headers;
    }
}
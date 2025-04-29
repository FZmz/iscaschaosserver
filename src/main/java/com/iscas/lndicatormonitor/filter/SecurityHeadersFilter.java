package com.iscas.lndicatormonitor.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 安全响应头过滤器
 * 添加各种安全相关的HTTP响应头，以提高应用程序的安全性
 */
@Order(1) // 确保这个过滤器首先执行
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // 阻止浏览器执行MIME类型嗅探（防止MIME类型混淆攻击）
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // 启用XSS过滤器（在检测到XSS攻击时，浏览器将阻止页面加载）
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // 内容安全策略（控制资源的加载）
        response.setHeader("Content-Security-Policy", 
                "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; img-src 'self' data:; " +
                "font-src 'self'; connect-src 'self' *; frame-src 'self'");
        
        // 引用策略（控制发送到其他域的Referer头）
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Adobe跨域策略（控制PDF和Flash如何请求数据）
        response.setHeader("X-Permitted-Cross-Domain-Policies", "none");
        
        // 设置IE下载选项（防止IE将响应保存为文件时执行）
        response.setHeader("X-Download-Options", "noopen");
        
        // HTTP严格传输安全（强制使用HTTPS）
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        
        // 点击劫持保护（控制页面是否可以在frame中显示）
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        
        // 权限策略（控制浏览器功能和API的使用）
        response.setHeader("Permissions-Policy", 
                "geolocation=(), camera=(), microphone=(), payment=()");
        
        // 跨域资源策略
        response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
        
        // 跨域打开者策略
        response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
        
        // 跨域嵌入器策略
        response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");

        // 如果是预检请求(OPTIONS)，直接返回200
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // 设置CORS响应头
            String origin = request.getHeader("Origin");
            if (origin != null) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
            } else {
                response.setHeader("Access-Control-Allow-Origin", "*");
            }
            
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers", 
                    "Content-Type, Authorization, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
            response.setHeader("Access-Control-Max-Age", "86400"); // 24小时

            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        // 继续执行过滤器链
        chain.doFilter(request, response);
    }
} 
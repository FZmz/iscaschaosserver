package com.iscas.lndicatormonitor.filter;
// import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// @Component  // 注释掉这个注解，避免重复的 CORS 配置
public class CustomCorsFilter implements Filter {
    // 受信任的域列表
    private final List<String> allowedOrigins = Arrays.asList(
        "http://10.147.245.41:8001",  
        "http://116.63.51.45:8001",   
        "http://localhost:8001",      
        "http://localhost:8002",      
        "http://127.0.0.1:8001",      
        "http://127.0.0.1:8002",      
        "http://localhost:5173"      
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        
        // 检查来源是否在受信任的列表中
        if (origin != null && (allowedOrigins.contains(origin) || origin.startsWith("http://localhost") || origin.startsWith("http://127.0.0.1"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Captcha-Id");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
        }
        
        chain.doFilter(request, response);
    }
}

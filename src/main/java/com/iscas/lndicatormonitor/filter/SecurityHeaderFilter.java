package com.iscas.lndicatormonitor.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import java.io.IOException;

public class SecurityHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        httpServletResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpServletResponse.setHeader("X-XSS-Protection", "1; mode=block");
        httpServletResponse.setHeader("Content-Security-Policy", "default-src 'self'");
        httpServletResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        httpServletResponse.setHeader("Referrer-Policy", "no-referrer");
        httpServletResponse.setHeader("X-Permitted-Cross-Domain-Policies", "none");
        httpServletResponse.setHeader("X-Download-Options", "noopen");

        chain.doFilter(request, response);
    }
}

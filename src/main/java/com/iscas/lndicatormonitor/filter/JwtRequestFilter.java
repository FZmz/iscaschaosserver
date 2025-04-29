package com.iscas.lndicatormonitor.filter;

import com.iscas.lndicatormonitor.domain.Role;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.dto.UsersDTO;
import com.iscas.lndicatormonitor.service.RoleService;
import com.iscas.lndicatormonitor.service.UsersService;
import com.iscas.lndicatormonitor.utils.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UsersService usersService;
    
    @Autowired
    private RoleService roleService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwtToken = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                log.error("JWT令牌解析失败", e);
            }
        }

        // 验证 token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Users user = usersService.selectByUserName(username);
                
                if (user != null && jwtTokenUtil.validateToken(jwtToken)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                log.error("认证过程中出错", e);
            }
        }
        
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/user/generateCaptcha") || 
               path.contains("/user/verifyCaptcha") || 
            //    path.contains("/api/user/login") ||
               path.contains("/user/loginnew");
    }
}
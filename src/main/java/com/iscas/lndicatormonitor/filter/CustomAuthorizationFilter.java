package com.iscas.lndicatormonitor.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.lndicatormonitor.domain.Role;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.dto.UsersDTO;
import com.iscas.lndicatormonitor.service.RoleService;
import com.iscas.lndicatormonitor.service.TokenBlacklistService;
import com.iscas.lndicatormonitor.service.UsersService;
import com.iscas.lndicatormonitor.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        String token = request.getHeader("Authorization");
        UsersService usersService = applicationContext.getBean(UsersService.class);
        RoleService roleService = applicationContext.getBean(RoleService.class);
        if (token != null && !token.isEmpty()) {
            // 使用 JwtTokenUtil 解析 token
            try {
               String jwtToken = token.substring(7);
               String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                if (username != null) {
                    Users user = null;
                    UsersDTO usersDTO = new UsersDTO();
                    try {
                        user = usersService.selectByUserName(username);
                        BeanUtils.copyProperties(user,usersDTO);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Role role = roleService.selectByUserId(user.getId());
                    usersDTO.setRole(role.getRoleType());
                    if (role.getRoleType() == 1) {
                        // 角色为 1 管理员，允许访问除 /audit/* 外的所有接口
                        if (!request.getRequestURI().startsWith("/audit/")) {
                            // 设置安全上下文，允许访问
                            setSecurityContext(usersDTO, request);
                        }
                    } else if (role.getRoleType() == 2) {
                        // 角色为 2 审计员，不允许访问除 /audit/* 外的所有接口
                        if (request.getRequestURI().startsWith("/api/audit/") || request.getRequestURI().startsWith("/api/user/") || request.getRequestURI().startsWith("/api/dashboard/")) {
                            // 返回无权限的响应
                            setSecurityContext(usersDTO, request);
                        }else {

                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            return;
                        }
                    }
                }
            } catch (MalformedJwtException e) {
                // 处理格式错误的令牌
                System.out.println("错误令牌"+e);
                return;
            } catch (IllegalArgumentException e) {
                // 处理其他错误
                System.out.println(e);
                return;
            } catch (ExpiredJwtException e) {
                // 处理令牌过期
                // 处理令牌过期
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置 401 状态码
                response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 设置响应内容类型为 JSON
                Map<String, Object> errorDetails = new HashMap<>();
                errorDetails.put("error", "TokenExpired");
                errorDetails.put("error_description", "令牌已过期");
                String jsonResponse = new ObjectMapper().writeValueAsString(errorDetails); // 将错误详情转换为 JSON 字符串
                response.getWriter().write(jsonResponse); // 将 JSON 响应写入响应体
                System.out.println("过期令牌");
                return;
            }

        }
        filterChain.doFilter(request, response);
    }

    private void setSecurityContext(UsersDTO user, HttpServletRequest request) {
        // 创建认证令牌并设置到安全上下文中
        // 根据实际情况创建合适的令牌对象
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

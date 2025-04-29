package com.iscas.lndicatormonitor.config;

import com.iscas.lndicatormonitor.filter.JwtRequestFilter;
import com.iscas.lndicatormonitor.filter.AuthorizationDebugFilter;
import com.iscas.lndicatormonitor.filter.RefererValidationFilter;
import com.iscas.lndicatormonitor.filter.SecurityHeadersFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Order(101)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private RefererValidationFilter refererValidationFilter;
    
    @Autowired
    private SecurityHeadersFilter securityHeadersFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().configurationSource(corsConfigurationSource()) // 启用 CORS 并应用配置
                .and()
                .authorizeRequests()
                .antMatchers("/user/generateCaptcha", "/user/verifyCaptcha", "/user/loginnew").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class) // 添加安全响应头过滤器
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(refererValidationFilter, JwtRequestFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        
        // 明确指定允许的源
        config.addAllowedOrigin("http://10.147.245.41:8001");  // 生产环境
        config.addAllowedOrigin("http://116.63.51.45:8001");   // 外网环境
        config.addAllowedOrigin("http://localhost:8001");      // 本地开发环境
        config.addAllowedOrigin("http://localhost:8002");      // 本地开发环境
        config.addAllowedOrigin("http://127.0.0.1:8001");      // 本地开发环境
        config.addAllowedOrigin("http://127.0.0.1:8002");      // 本地开发环境
        config.addAllowedOrigin("http://localhost:5173");      // 本地开发环境
        
        // 如果需要支持 HTTPS，也添加对应的 HTTPS 地址
        config.addAllowedOrigin("https://10.147.245.41:8001");
        config.addAllowedOrigin("https://116.63.51.45:8001");
        config.addAllowedOrigin("https://localhost:8001");
        config.addAllowedOrigin("https://localhost:8002");
        config.addAllowedOrigin("https://127.0.0.1:8001");
        config.addAllowedOrigin("https://127.0.0.1:8002");
        config.addAllowedOrigin("https://localhost:5173");

        config.addAllowedHeader("*");     // 允许所有头
        config.addAllowedMethod("*");     // 允许所有方法
        config.setMaxAge(3600L);          // 预检请求的有效期
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
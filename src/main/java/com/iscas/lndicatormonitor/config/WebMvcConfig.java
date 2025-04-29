package com.iscas.lndicatormonitor.config;

import com.iscas.lndicatormonitor.interceptor.PermissionInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebMvcConfig.class);

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("POST", "GET", "PUT", "DELETE")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("配置权限拦截器");
        System.out.println(registry.toString());
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/loginnew", "/user/logout","/user/isFirstLogin","/user/generateCaptcha","/user/verifyCaptcha");
        log.info("权限拦截器配置完成，拦截路径: /**，排除路径: /user/login, /user/logout");
    }
}

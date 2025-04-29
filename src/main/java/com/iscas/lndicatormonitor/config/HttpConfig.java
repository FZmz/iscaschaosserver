package com.iscas.lndicatormonitor.config;

import com.iscas.lndicatormonitor.utils.CustomJacksonHttpMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

@Configuration
public class HttpConfig {

    @Value("${coroot.url}")
    private String corootUrl;

    @Value("${coroot.cookie}")
    private String cookie;

    private static final Logger log = LoggerFactory.getLogger(CustomClientHttpRequestInterceptor.class);

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // 设置超时
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);  // 30秒连接超时
        factory.setReadTimeout(30000);     // 30秒读取超时
        restTemplate.setRequestFactory(factory);
        // 添加拦截器
        restTemplate.setInterceptors(Collections.singletonList(new CustomClientHttpRequestInterceptor()));
        // 添加条件拦截器
        restTemplate.getInterceptors().add((request, body, execution) -> {
            if (request.getURI().toString().startsWith(corootUrl)) {
                request.getHeaders().add(HttpHeaders.COOKIE, cookie);
            } else {
            }
            return execution.execute(request, body);
        });
        // 添加错误处理
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                        || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                log.error("响应错误: {} {}", response.getStatusCode(), response.getStatusText());
            }
        });
        return restTemplate;
    }
}
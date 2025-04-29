package com.iscas.lndicatormonitor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(CustomClientHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        long startTime = System.currentTimeMillis();
        log.info("===========================请求开始===========================");
        log.info("请求URI : {}", request.getURI());
        log.info("请求方法 : {}", request.getMethod());
        log.info("请求头 : {}", request.getHeaders());

        try {
            ClientHttpResponse response = execution.execute(request, body);
            long endTime = System.currentTimeMillis();

            log.info("响应状态码 : {}", response.getStatusCode());
            log.info("响应头 : {}", response.getHeaders());
            log.info("请求耗时 : {} ms", endTime - startTime);
            log.info("===========================请求结束===========================");

            return response;
        } catch (Exception e) {
            log.error("请求异常: ", e);
            throw e;
        }
    }
}
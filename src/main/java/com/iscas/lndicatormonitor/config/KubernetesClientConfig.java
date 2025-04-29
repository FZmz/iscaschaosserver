package com.iscas.lndicatormonitor.config;

import io.fabric8.kubernetes.client.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesClientConfig {
    @Value("${kubernetes.api-url}")
    private String apiUrl;

    @Value("${kubernetes.token}")
    private String token;

    @Value("${kubernetes.verify-ssl}")
    private boolean verifySsl;
    @Bean
    public KubernetesClient kubernetesClient() {
        // 配置 Kubernetes Client
        Config config = new Config();
        config.setMasterUrl(apiUrl);
        config.setOauthToken(token);
        config.setTrustCerts(!verifySsl);

        return new DefaultKubernetesClient(config);
    }
}

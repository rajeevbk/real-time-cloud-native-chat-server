package com.rajeevbk.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Add an interceptor to the RestTemplate for token propagation
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                String tokenValue = jwtAuth.getToken().getTokenValue();
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
            }

            return execution.execute(request, body);
        }));

        // MERGED: Added the timeout configuration from your BeanConfiguration
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setReadTimeout(30000);
        rf.setConnectTimeout(30000);
        restTemplate.setRequestFactory(rf);

        return restTemplate;
    }
}
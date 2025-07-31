package com.rajeevbk.configuration;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides a clean, configured Keycloak Admin Client bean.
 * This version uses the secure client_credentials grant type, which is standard
 * for backend service-to-service communication.
 */
@Configuration
public class KeycloakAdminClientConfig {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${keycloak.admin-client-secret}")
    private String adminClientSecret;

    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .grantType("client_credentials")
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
                .build();
    }
}

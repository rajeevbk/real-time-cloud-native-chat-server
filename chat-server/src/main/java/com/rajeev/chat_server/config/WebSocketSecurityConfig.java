package com.rajeev.chat_server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * This configuration class secures the WebSocket channel by intercepting
 * connection requests and validating the JWT token from the STOMP headers.
 */
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // Give this config high priority
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtDecoder jwtDecoder; // Autowire the JwtDecoder configured by Spring Boot

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // Check if it's a CONNECT command
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Look for the Authorization header
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);

                        try {
                            // Decode and validate the JWT
                            Jwt jwt = jwtDecoder.decode(token);

                            // Convert the JWT to a Spring Security Authentication object
                            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                            Authentication authentication = converter.convert(jwt);

                            // Set the authenticated user on the session
                            accessor.setUser(authentication);
                        } catch (Exception e) {
                            // Handle token validation failure
                            System.err.println("WebSocket Auth Error: " + e.getMessage());
                            // You could throw an exception here to reject the connection
                        }
                    }
                }
                return message;
            }
        });
    }
}
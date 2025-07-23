package com.rajeev.chat_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/**
 * Configures WebSocket messaging with STOMP and a RabbitMQ message broker.
 * This class enables real-time communication between the server and connected clients.
 */
@Configuration
@EnableWebSocketMessageBroker // Enables WebSocket message handling, backed by a message broker.
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    // These values are injected from your application.properties file.
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.stomp-port}")
    private int rabbitmqStompPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    /**
     * Registers the STOMP endpoints, mapping each to a specific URL and enabling
     * SockJS fallback options. This is the entry point for WebSocket connections.
     * @param registry The STOMP endpoint registry.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The /ws-chat endpoint is what the client will connect to for the WebSocket handshake.
        // setAllowedOriginPatterns("*") is used for development to allow all origins.
        // withSockJS() provides a fallback for browsers that don't support WebSockets.
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * Configures the message broker that will be used to route messages from one client to another.
     * @param registry The message broker registry.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Defines the prefix for messages that are bound for application methods
        // annotated with @MessageMapping. For example, a message sent to "/app/chat.sendMessage"
        // will be routed to a method like @MessageMapping("/chat.sendMessage").
        registry.setApplicationDestinationPrefixes("/app");

        // Configures a full-featured STOMP broker relay that connects to an external
        // message broker (RabbitMQ) to handle subscriptions and broadcasting.
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(rabbitmqHost)
                .setRelayPort(rabbitmqStompPort)
                .setClientLogin(rabbitmqUsername) // Note: use setClientLogin/Passcode for RabbitMQ
                .setClientPasscode(rabbitmqPassword)
                .setSystemLogin(rabbitmqUsername) // For the broker's own connection
                .setSystemPasscode(rabbitmqPassword);

        // Configures the prefix for user-specific destinations. Spring will use this
        // to create unique destinations for each user session, enabling private messaging.
        // For example, a user can subscribe to "/user/queue/private" to receive private messages.
        registry.setUserDestinationPrefix("/user");
    }
}
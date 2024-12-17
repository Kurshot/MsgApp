package com.MsgApp.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  // WebSocket desteğini etkinleştirir
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /topic ve /queue önekli hedeflere abone olunmasına izin verir
        // /topic: Grup mesajları ve duyurular için
        // /queue: Özel mesajlar için
        config.enableSimpleBroker("/topic", "/queue");

        // İstemciden gelen mesajların /app öneki ile başlaması gerektiğini belirtir
        config.setApplicationDestinationPrefixes("/app");

        // Kullanıcıya özel hedefler için önek belirleme
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket bağlantı noktasını tanımlar
        registry.addEndpoint("/ws")
                // CORS yapılandırması - geliştirme aşamasında tüm kaynaklara izin verilir
                .setAllowedOriginPatterns("*")
                // WebSocket desteklenmeyen tarayıcılar için SockJS fallback'i etkinleştirir
                .withSockJS();
    }
}

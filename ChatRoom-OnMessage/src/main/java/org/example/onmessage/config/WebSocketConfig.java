package org.example.onmessage.config;

import lombok.RequiredArgsConstructor;
import org.example.onmessage.handler.OnMessageHandler;
import org.example.onmessage.inteceptor.WebsocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/4
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final OnMessageHandler onMessageHandler;
    private final WebsocketInterceptor websocketInterceptor;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(onMessageHandler, "/ws/onMessage").addInterceptors(websocketInterceptor).setAllowedOrigins("*");
    }
}

package com.mpole.wearable.config;

import com.mpole.wearable.component.AdminWebsocketHandler;
import com.mpole.wearable.component.MyWebsocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebsocketConfig {

    @Bean
    public HandlerMapping webSocketMapping(MyWebsocketHandler webSocketHandler, AdminWebsocketHandler adminWebsocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        // "/ws/echo" 엔드포인트에 WebSocketHandler를 매핑합니다.
        map.put("/ws/echo", webSocketHandler);
        map.put("/ws/admin",adminWebsocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        // 다른 HandlerMapping보다 우선순위 높게 설정
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}

package com.mpole.wearable.component;

import com.mpole.imp.framework.redis.RedisSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class AdminWebsocketHandler implements WebSocketHandler {

    @Autowired
    RedisSubscriber redisSubscriber;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> testFlux = redisSubscriber.subscribeToChannel("test").map(session::textMessage);
        //Flux<WebSocketMessage> alarmFlux = redisSubscriber.subscribeToChannel("alarm").map(session::textMessage);
        //Flux<WebSocketMessage> merged = Flux.merge(testFlux, alarmFlux);
        return session.send(testFlux);
//        return session.send(session.receive()
//                .map(WebSocketMessage::getPayloadAsText)
//                .doOnNext(msg -> System.out.println("message-receive : " + msg))
//                .doOnNext(msg -> System.out.println("???"+msg))
//                .map(session::textMessage))
//                .doOnError(e -> {
//                    System.err.println("WebSocket error: " + e.getMessage());
//                    e.printStackTrace();
//                });
    }
}

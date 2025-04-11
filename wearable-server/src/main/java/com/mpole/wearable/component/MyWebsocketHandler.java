package com.mpole.wearable.component;

import com.mpole.imp.framework.redis.RedisPublisher;
import com.mpole.imp.framework.redis.RedisRepository;
import com.mpole.imp.framework.utils.JsonHelper;
import com.mpole.wearable.dto.SensorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Component
public class MyWebsocketHandler implements WebSocketHandler {

    @Autowired
    RedisPublisher redisPublisher;
    @Autowired
    RedisRepository redisRepository;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> output = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(msg -> System.out.println("message-receive : " + msg))
//                .flatMap(msg -> {
//                    HashMap data = JsonHelper.fromJson(msg, HashMap.class);
//                    String key = data.get("serial").toString()+":"+data.get("sensor").toString();
//                    return redisRepository.lpush(key,data).thenReturn(msg);
//                }) // redis 에 데이타 저장
                .flatMap(this::eventRuleCheck)
                //.doOnNext(msg -> System.out.println("???"+msg))
                .map(session::textMessage)
                .onErrorResume(e ->{
                    e.printStackTrace();
                    return Mono.empty();
                });

        return session.send(output)
                .doOnTerminate(() -> System.out.println("WebSocket session closed"));
    }

    public Mono<String> eventRuleCheck(String msg){
        return Mono.fromCallable(() ->{
            //HashMap map = JsonHelper.fromJson(msg, HashMap.class);

            SensorDTO sensorDTO = JsonHelper.fromJson(msg,SensorDTO.class);
            String returnValue = JsonHelper.toJson(sensorDTO);
            return redisPublisher.publishMessage("test",returnValue).thenReturn(returnValue);
//            switch (map.get("sensor")) {
//
//            }

//            if ("bpm".equals(map.get("sensor")) &&
//                    (Double) map.get("value") > 60) {
//                map.put("type","alarm");
//            }
//            String returnValue = JsonHelper.toJson(map);
//            return redisPublisher.publishMessage("test",returnValue).thenReturn(returnValue);
        }).flatMap(m->m);
    }
}

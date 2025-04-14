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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
                //.doOnNext(msg -> System.out.println("message-receive : " + msg))
//                .flatMap(msg -> {
//                    HashMap data = JsonHelper.fromJson(msg, HashMap.class);
//                    String key = data.get("serial").toString()+":"+data.get("sensor").toString();
//                    return redisRepository.lpush(key,data).thenReturn(msg);
//                }) // redis 에 데이타 저장
                .flatMap(this::eventRuleCheck)
                .doOnNext(msg -> System.out.println("message-receive : " + msg))
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
        return Mono.defer(() ->{
            SensorDTO sensorDTO = JsonHelper.fromJson(msg,SensorDTO.class);
            switch (sensorDTO.getSensor()) {
                case "gps":
                    //gpsRuleCheck();
                    break;
                case "step":
                    //stepRuleCheck();
                    break;
                case "bpm":
                    sensorDTO = bpmRuleCheck(sensorDTO);
                    break;
                case "beacon":
                    sensorDTO=beaconRuleCheck(sensorDTO);
                    break;
                default:
                    break;
            }
            String returnValue = JsonHelper.toJson(sensorDTO);
            return redisPublisher.publishMessage("test",returnValue).thenReturn(returnValue);
        });
    }

    public SensorDTO bpmRuleCheck(SensorDTO sensorDTO){
        if ((double) sensorDTO.getValue() > 60) sensorDTO.setType("alarm");
        return sensorDTO;
    }

    public SensorDTO beaconRuleCheck(SensorDTO sensorDTO){
        List<?> valueList = (List<?>) sensorDTO.getValue();
        sensorDTO.setValue(valueList.stream().map(e->(HashMap) e).filter(v -> (int) v.get("rssi") > -60).toList());
        return sensorDTO;
    }

}

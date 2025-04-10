package com.mpole.wearable.controller;

import com.mpole.imp.framework.redis.RedisRepository;
import com.mpole.imp.framework.utils.LogHelper;
import com.mpole.wearable.dto.DeviceDTO;
import com.mpole.wearable.dto.Response;
import com.mpole.wearable.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

@RestController
@Tag(name = "API", description = "API 정리")
public class LoginController {

    @Autowired
    RedisRepository redisRepository;

    @PostMapping("/api/login")
    @ResponseBody
    public String login(@RequestBody HashMap body) {
        System.out.println("login");
        String id = body.get("id").toString();
        String password = body.get("password").toString();
        LogHelper.debug("login in body :",id, password);

        // Implement your login logic here
        return "Login successful!";
    }


    @Operation(summary = "사용자 조회", description = "모든 사용자 정보를 Redis에서 조회")
    @PostMapping("/api/getAllUsers")
    @ResponseBody
    public Mono<Response<List<UserDTO>>> getAllUsers() {
        return redisRepository.hgetall("user", UserDTO.class)
                .collectList()
                .map(users-> Response.success(users));
    }

    @Operation(summary = "사용자 등록 (Redis에 저장)", description = "사용자 정보를 Redis에 저장하고, 등록 결과를 반환합니다.")
    @PostMapping("/api/registUser")
    @ResponseBody
    public Mono<Response> regist(@RequestBody UserDTO userDTO) {
        return redisRepository.hset("user", userDTO.getSerial(), userDTO)
                .then(Mono.just(Response.success(userDTO)));
    }



    @Operation(summary = "기기확인 (Redis에서 조회)", description = "기기정보 를 Redis에서 조회해서 사전에 등록된 경우 확인을 보내준다.")
    @PostMapping("/api/verifyUserDevice")
    @ResponseBody
    public Mono<Response<UserDTO>> verifyUserDevice(@RequestBody DeviceDTO deviceDTO) {
//        return redisRepository.hexists("user", deviceDTO.getSerial())
//                .doOnError(e->LogHelper.debug("error::",e))
//                .flatMap(exists -> exists?
//                        Mono.just(Response.success(true)) : Mono.just(Response.fail(ErrorCode.DEVICE_NOT_EXIST)));
//                .map(Response::success);

        return redisRepository.hget("user", deviceDTO.getSerial(),UserDTO.class)
                .doOnError(e->LogHelper.debug("error::",e))
                .switchIfEmpty(Mono.error(new RuntimeException(("There is no device"))))
                .map(Response::success);
    }
}

package com.mpole.wearable.exception;

import com.mpole.imp.framework.utils.JsonHelper;
import com.mpole.wearable.dto.Response;
import com.mpole.wearable.dto.type.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-2)
public class GlobalWebExceptionHandler implements WebExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalWebExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();

        Response customResponse;
        HttpStatus status;

        if (ex instanceof StatusOkException) {
            status = HttpStatus.OK;
            customResponse = Response.fail(((StatusOkException) ex).getErrorCode());
        }else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            customResponse = Response.fail(ErrorCode.UNEXPECTED_ERROR);
        }
        response.setStatusCode(status);
        logger.error("[Global Error] {} {} - Error: {}", request.getMethod(), request.getURI(), ex.getMessage(), ex);

        //JSON 변환을 비동기적으로 실행함
        return Mono.fromCallable(() -> JsonHelper.toJson(customResponse))
                .map(jsonResponse -> response.bufferFactory().wrap((jsonResponse).getBytes(StandardCharsets.UTF_8)))
                .flatMap(a->response.writeWith(Mono.just(a)));

    }
}

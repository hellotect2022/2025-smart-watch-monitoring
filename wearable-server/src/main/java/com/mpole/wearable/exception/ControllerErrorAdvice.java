package com.mpole.wearable.exception;

import com.mpole.wearable.dto.Response;
import com.mpole.wearable.dto.type.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

//@RestControllerAdvice
public class ControllerErrorAdvice {
    private final Logger logger = LoggerFactory.getLogger(ControllerErrorAdvice.class);
    private final Marker marker = MarkerFactory.getMarker("STDOUT");

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public Mono<Object> handleGenericException(RuntimeException e) {
        return Mono.error(e)
                .onErrorResume(err -> {
                    logger.error("Unhandled exception: {}", err.getMessage(), err);
                    return Mono.just(Response.fail( ErrorCode.UNEXPECTED_ERROR));
                });
//        return Mono.just(Response.fail(ErrorCode.UNEXPECTED_ERROR))
//                .doFirst(()->{
//                    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
//                    logger.error(stackTrace[0].toString());
//                    logger.error(stackTrace[1].toString());
//                    int loop = Math.min(e.getStackTrace().length, 15);
//                    for (int i=0; i<loop; i++) {
//                        logger.error("-> {}", e.getStackTrace()[i].toString());
//                    }
//                });
    }
    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.OK)
    public Mono<Object> CustomException(CustomException e) {
        return Mono.error(e)
                .onErrorResume(err -> {
                    logger.error("Unhandled exception: {}", err.getMessage(), err);
                    return Mono.just(Response.fail(ErrorCode.UNEXPECTED_ERROR));
                });
    }


}

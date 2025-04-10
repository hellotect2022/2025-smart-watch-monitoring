package com.mpole.wearable.exception;


import com.mpole.wearable.dto.type.ErrorCode;
import com.mpole.wearable.dto.type.Status;

public class StatusOkException extends CustomException{
    public StatusOkException(String message) {
        super(message);
    }

    public StatusOkException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StatusOkException(Status status, ErrorCode errorCode, String message) {
        super(status, errorCode, message);
    }
}

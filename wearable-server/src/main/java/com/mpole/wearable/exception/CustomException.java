package com.mpole.wearable.exception;


import com.mpole.wearable.dto.type.ErrorCode;
import com.mpole.wearable.dto.type.Status;

public class CustomException extends RuntimeException {
    private Status status;
    private ErrorCode errorCode;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.status = Status.FAIL;
        this.errorCode = errorCode;
    }

    public CustomException(Status status, ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.status = status;
        this.errorCode = errorCode;
    }

    public CustomException(Status status, ErrorCode errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public Status getStatus() {
        return status;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

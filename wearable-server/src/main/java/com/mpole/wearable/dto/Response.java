package com.mpole.wearable.dto;


import com.mpole.wearable.dto.type.ErrorCode;
import com.mpole.wearable.dto.type.Status;

public class Response<T> {
    private Status status;
    private  T data;
    private ErrorCode errorCode;
    private String description;
    
    public Response(Status status, T data) {
        this.status = status;
        if (data instanceof ErrorCode) {
            this.errorCode = (ErrorCode) data;
            this.description = errorCode.getDescription();
        }else {
            this.data = data;
        }
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(Status.SUCCESS, data);
    }

    public static Response fail(ErrorCode errorCode) {
        return new Response<>(Status.FAIL, errorCode);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }
}

package com.mpole.wearable.dto.type;

public enum ErrorCode {
    INVALID_REQUEST("Invalid request"),
    NOT_FOUND("Not found"),
    UNEXPECTED_ERROR("An unexpected error occurred"),
    FORBIDDEN("Forbidden"),
    UNAUTHORIZED("Unauthorized"),
    SOME_ERROR("Some error occurred"),
    DEVICE_NOT_EXIST("There is no device!!")
    ;
    private String description;
    ErrorCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

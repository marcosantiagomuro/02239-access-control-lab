package com.datasec.utils;

import lombok.Getter;

@Getter
public class SystemException extends RuntimeException {
    private String errorCode;
    private String errorMessage;

    public SystemException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}

// CODE 10   ERROR SESSION    <-- sessionId not found
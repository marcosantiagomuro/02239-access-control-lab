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
// CODE 20   ERROR USERNAME   <-- special characters in username login
// CODE 30   ERROR AUTHORIZATION   <-- user not authorized
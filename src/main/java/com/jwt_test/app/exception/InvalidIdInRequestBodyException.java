package com.jwt_test.app.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidIdInRequestBodyException extends RuntimeException {
    public InvalidIdInRequestBodyException(String message, Object id) {
        super(message + " Id: " +  id);
    }
}

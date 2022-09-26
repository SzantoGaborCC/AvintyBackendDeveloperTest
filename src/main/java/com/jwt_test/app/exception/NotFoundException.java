package com.jwt_test.app.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message, Object id) {
        super(message + " Id: " +  id);
    }
}

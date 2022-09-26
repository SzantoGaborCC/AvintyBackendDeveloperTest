package com.jwt_test.app.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidEmployeeIdInRequestBodyException extends InvalidIdInRequestBodyException {
    public InvalidEmployeeIdInRequestBodyException(Integer id) {
        super("Invalid employee specified in request body!", id);
    }
}

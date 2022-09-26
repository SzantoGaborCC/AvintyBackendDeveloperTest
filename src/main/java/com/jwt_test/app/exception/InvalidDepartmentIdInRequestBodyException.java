package com.jwt_test.app.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidDepartmentIdInRequestBodyException extends InvalidIdInRequestBodyException {
    public InvalidDepartmentIdInRequestBodyException(Integer id) {
        super("Invalid department specified in request body!", id);
    }
}

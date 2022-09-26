package com.jwt_test.app.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmployeeNotFoundException extends NotFoundException {
    public EmployeeNotFoundException(Object id) {
        super("Employee was not found!", id);
    }
}

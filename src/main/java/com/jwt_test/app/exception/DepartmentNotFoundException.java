package com.jwt_test.app.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DepartmentNotFoundException extends NotFoundException {
    public DepartmentNotFoundException(Object id) {
        super("Department was not found!", id);
    }
}

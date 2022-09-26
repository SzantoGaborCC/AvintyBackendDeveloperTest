package com.jwt_test.app.exception_handler;

import com.jwt_test.app.dto.response.ServerMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
@Slf4j
public class SqlExceptionHandler {
    @ExceptionHandler({SQLException.class})
    public ResponseEntity<?> handleSqlException(Exception e) {
        log.warn(e.getMessage());
        ServerMessageDto serverMessageDto = new ServerMessageDto(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(serverMessageDto);
    }
}


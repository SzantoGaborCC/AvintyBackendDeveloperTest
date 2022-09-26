package com.jwt_test.app.exception_handler;

import com.jwt_test.app.dto.response.ServerMessageDto;
import com.jwt_test.app.exception.InvalidIdInRequestBodyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class InvalidIdInRequestBodyExceptionHandler {
    @ExceptionHandler({InvalidIdInRequestBodyException.class})
    public ResponseEntity<?> handleInvalidIdInRequestBodyException(Exception e) {
        log.warn(e.getMessage());
        ServerMessageDto serverMessageDto = new ServerMessageDto(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(serverMessageDto);
    }
}


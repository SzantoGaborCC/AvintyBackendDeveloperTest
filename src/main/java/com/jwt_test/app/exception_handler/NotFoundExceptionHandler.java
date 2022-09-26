package com.jwt_test.app.exception_handler;

import com.jwt_test.app.dto.response.ServerMessageDto;
import com.jwt_test.app.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class NotFoundExceptionHandler {
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        log.warn(e.getMessage());
        ServerMessageDto serverMessageDto = new ServerMessageDto(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(serverMessageDto);
    }
}


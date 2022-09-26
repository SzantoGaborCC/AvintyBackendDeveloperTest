package com.jwt_test.app.utility;

import com.jwt_test.app.dto.response.ServerMessageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.stream.Collectors;

public class Utility {
    public static ResponseEntity<ServerMessageDto> createBadRequestMessageResponseEntity(BindingResult result) {
        return ResponseEntity.badRequest().body(new ServerMessageDto(
                result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage))
                        .toString())
        );
    }
}

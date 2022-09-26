package com.jwt_test.app.security.exception_handlers;

import com.jwt_test.app.dto.response.ServerMessageDto;
import com.jwt_test.app.security.exception.TokenRefreshException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class TokenRefreshExceptionHandler {
    @ExceptionHandler(value = {TokenRefreshException.class})
    public void handleTokenRefreshException(HttpServletRequest request, HttpServletResponse response,
                          Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ServerMessageDto serverMessageDto = new ServerMessageDto(e.getMessage());
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(serverMessageDto)
        );
    }
}

package com.jwt_test.app.security.exception_handlers;

import com.jwt_test.app.dto.response.ServerMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class AccessDeniedExceptionHandler {
    @ExceptionHandler(value = {AccessDeniedException.class})
    public void handleAccessDeniedException(HttpServletRequest request, HttpServletResponse response,
                                            Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ServerMessageDto serverMessageDto = new ServerMessageDto("Forbidden! " + e.getMessage());
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(serverMessageDto)
        );
    }
}

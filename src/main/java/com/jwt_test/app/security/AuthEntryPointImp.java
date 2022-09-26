package com.jwt_test.app.security;

import com.jwt_test.app.dto.response.ServerMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthEntryPointImp implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ServerMessageDto serverMessageDto = new ServerMessageDto("Unauthorized! " + authException.getMessage());
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(serverMessageDto)
        );
    }
}
package com.jwt_test.app.security.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JwtLoginResponseDto {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private Integer id;
    private String email;
    private List<String> roles;
    public JwtLoginResponseDto(String accessToken, String refreshToken, Integer id, String email, List<String> roles) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.email = email;
        this.roles = roles;
    }
}

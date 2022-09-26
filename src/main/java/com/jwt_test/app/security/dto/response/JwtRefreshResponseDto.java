package com.jwt_test.app.security.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtRefreshResponseDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    public JwtRefreshResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}

package com.jwt_test.app.security.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.jwt_test.app.security.entity.RefreshToken;
import com.jwt_test.app.security.exception.TokenRefreshException;
import com.jwt_test.app.repository.EmployeeRepository;
import com.jwt_test.app.security.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {
    @Value("${jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Integer employeeId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setEmployee(employeeRepository.findById(employeeId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public void deleteByEmployeeId(Integer employeeId) {
        refreshTokenRepository.deleteByEmployee(employeeRepository.findById(employeeId).get());
    }
}

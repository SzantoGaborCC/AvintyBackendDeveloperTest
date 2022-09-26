package com.jwt_test.app.security.repository;

import com.jwt_test.app.entity.Employee;
import com.jwt_test.app.security.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByEmployee(Employee employee);
}

package com.jwt_test.app.security.auditor;

import com.jwt_test.app.entity.Employee;
import com.jwt_test.app.exception.EmployeeNotFoundException;
import com.jwt_test.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Profile("WITH_AUTH")
@Component
@EnableJpaAuditing(auditorAwareRef = "employeeAuditor")
public class EmployeeAuditor implements AuditorAware<Employee> {
    @Autowired
    EmployeeRepository employeeRepository;

    private final Map<String, Employee> employeeCache = new HashMap<>(); //to reduce database traffic

    @Override
    public Optional<Employee> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String email = authentication.getName(); //username is the unique email
        return Optional.of(
                employeeCache.computeIfAbsent(
                    email,
                    k -> employeeRepository.findByEmail(k)
                        .orElseThrow(() -> new EmployeeNotFoundException(k))));
    }
}

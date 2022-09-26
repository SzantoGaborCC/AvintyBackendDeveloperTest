package com.jwt_test.app.security.service;

import com.jwt_test.app.entity.Employee;
import com.jwt_test.app.repository.EmployeeRepository;
import com.jwt_test.app.security.EmployeeDetails;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public EmployeeDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password!") {
                });
        return new EmployeeDetails(employee);
    }
}

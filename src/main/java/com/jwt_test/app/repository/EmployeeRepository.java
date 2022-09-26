package com.jwt_test.app.repository;

import com.jwt_test.app.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByEmail(String email);

    <T> Optional<T> findById(Integer id, Class<T> type);

    @Query("SELECT e FROM Employee e")
    <T> List<T> getAllEmployees(Class<T> type);
}
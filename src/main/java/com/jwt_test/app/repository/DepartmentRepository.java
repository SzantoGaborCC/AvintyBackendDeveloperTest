package com.jwt_test.app.repository;

import com.jwt_test.app.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    @Query("SELECT d.name FROM Department d WHERE LOWER(d.name) LIKE CONCAT('%',LOWER(COALESCE(:name,'')),'%') ORDER BY d.name")
    List<String> findNamesLikeNameOrderByName(String name);

    <T> Optional<T> findById(Integer id, Class<T> type);

    @Query("SELECT d FROM Department d")
    <T> List<T> getAllDepartments(Class<T> type);
}
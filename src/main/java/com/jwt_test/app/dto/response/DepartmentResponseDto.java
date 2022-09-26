package com.jwt_test.app.dto.response;

import com.jwt_test.app.entity.Department;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
        scope = DepartmentResponseDto.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name")
public class DepartmentResponseDto {
    private Integer id;
    private String name;
    private EmployeeIdAndFullNameResponseDto manager;
    private Set<EmployeeIdAndFullNameResponseDto> employees = new HashSet<>();

    public DepartmentResponseDto(Department department) {
        this.id = department.getId();
        this.name = department.getName();
        this.manager = department.getManager() == null ?
                null :
                new EmployeeIdAndFullNameResponseDto(department.getManager());
        this.employees = department.getEmployees().stream()
                .map(EmployeeIdAndFullNameResponseDto::new)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentResponseDto that = (DepartmentResponseDto) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package com.jwt_test.app.dto.response;

import com.jwt_test.app.entity.Employee;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
        scope = EmployeeResponseDto.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "fullName")
public class EmployeeResponseDto {
    private Integer id;
    private String email;
    private String fullName;
    private DepartmentIdAndNameResponseDto department;
    private Boolean isActive;

    public EmployeeResponseDto(Employee employee) {
        this.id = employee.getId();
        this.email = employee.getEmail();
        this.fullName = employee.getFullName();
        this.department = employee.getDepartment() == null ?
                null :
                new DepartmentIdAndNameResponseDto(employee.getDepartment());
        this.isActive = employee.getIsActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeResponseDto that = (EmployeeResponseDto) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

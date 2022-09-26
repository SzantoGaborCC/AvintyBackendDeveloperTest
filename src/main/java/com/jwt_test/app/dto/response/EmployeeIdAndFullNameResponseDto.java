package com.jwt_test.app.dto.response;

import com.jwt_test.app.entity.Employee;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(
        scope = EmployeeIdAndFullNameResponseDto.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "fullName")
public class EmployeeIdAndFullNameResponseDto {
    private Integer id;
    private String fullName;

    public EmployeeIdAndFullNameResponseDto(Employee employee) {
        this.id = employee.getId();
        this.fullName = employee.getFullName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeIdAndFullNameResponseDto that = (EmployeeIdAndFullNameResponseDto) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

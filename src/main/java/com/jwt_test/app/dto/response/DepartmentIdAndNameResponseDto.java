package com.jwt_test.app.dto.response;

import com.jwt_test.app.entity.Department;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        scope = DepartmentIdAndNameResponseDto.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name")
public class DepartmentIdAndNameResponseDto {
    private Integer id;
    private String name;

    public DepartmentIdAndNameResponseDto(Department department) {
        this.id = department.getId();
        this.name = department.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentIdAndNameResponseDto that = (DepartmentIdAndNameResponseDto) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

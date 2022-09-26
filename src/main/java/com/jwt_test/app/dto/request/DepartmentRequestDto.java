package com.jwt_test.app.dto.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentRequestDto {
    @NotEmpty(message = "Department name cannot be empty!")
    @Pattern(regexp = ".*\\S.*", message = "Department names cannot contain only whitespace characters!")
    @Size(min = 1, max = 100, message = "Department names must be between 1 and 100 characters long!")
    private String name;
    private Integer managerId;

    private List<Integer> employeeIds = new ArrayList<>();

    public DepartmentRequestDto(String name, Integer managerId) {
        this.name = name;
        this.managerId = managerId;
    }
}

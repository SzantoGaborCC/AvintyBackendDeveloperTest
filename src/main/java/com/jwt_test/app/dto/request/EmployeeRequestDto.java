package com.jwt_test.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDto {
    @Email(message = "Email address must be valid!")
    private String email;

    @NotEmpty(message = "Password cannot be empty!")
    @Pattern(regexp = ".*\\S.*", message = "Password cannot contain only whitespace characters!")
    @Size(min = 8, max = 66,  message = "Password must be between 8 and 66 characters long!")
    private String password;

    @NotEmpty(message = "Full name cannot be empty!")
    @Pattern(regexp = ".*\\S.*", message = "Full name cannot contain only whitespace characters!")
    @Size(min = 4, max = 200, message = "Full name must be between 4 and 200 characters long!")
    private String fullName;

    private Integer departmentId;

    @NotNull(message = "You have to set the active status!")
    private Boolean isActive;

    @Size(min = 1, message = "You have to specify at least one role for the user!")
    private List<String> roles = new ArrayList<>();
}

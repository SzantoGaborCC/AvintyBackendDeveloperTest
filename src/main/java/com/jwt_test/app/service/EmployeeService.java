package com.jwt_test.app.service;

import com.jwt_test.app.dto.request.EmployeeRequestDto;
import com.jwt_test.app.dto.response.EmployeeResponseDto;
import com.jwt_test.app.entity.Department;
import com.jwt_test.app.entity.Employee;
import com.jwt_test.app.entity.EmployeeRole;

import com.jwt_test.app.exception.EmployeeNotFoundException;
import com.jwt_test.app.exception.InvalidDepartmentIdInRequestBodyException;
import com.jwt_test.app.exception.InvalidEmployeeIdInRequestBodyException;
import com.jwt_test.app.repository.DepartmentRepository;
import com.jwt_test.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    private Environment environment;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<EmployeeResponseDto> getAllEmpoyees() {
        return employeeRepository.getAllEmployees(EmployeeResponseDto.class);
    }

    public EmployeeResponseDto findById(Integer id) {
        return employeeRepository.findById(id, EmployeeResponseDto.class)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public void add(EmployeeRequestDto employeeRequestDto) {
        Employee employee = createEmployeeFromDto(employeeRequestDto);
        employeeRepository.save(employee);
    }

    public void update(int id, EmployeeRequestDto employeeRequestDto) {
        if (employeeRepository.existsById(id)) {
            Employee employee = createEmployeeFromDto(employeeRequestDto);
            employee.setId(id);
            employeeRepository.save(employee);
        } else {
            throw new InvalidEmployeeIdInRequestBodyException(id);
        }
    }

    public void deleteById(int id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        Department department = employee.getDepartment();
        if (department != null && employee.equals(department.getManager())) {
            department.setManager(null);
            departmentRepository.save(department);
        }
        employeeRepository.delete(employee);
    }

    private Employee createEmployeeFromDto(EmployeeRequestDto employeeRequestDto) {
        return new Employee(
                employeeRequestDto.getEmail(),
                passwordEncoder.encode(employeeRequestDto.getPassword()),
                employeeRequestDto.getFullName(),
                employeeRequestDto.getDepartmentId() == null ? null :
                        departmentRepository.findById(employeeRequestDto.getDepartmentId())
                                .orElseThrow(() -> new InvalidDepartmentIdInRequestBodyException(employeeRequestDto.getDepartmentId())),
                employeeRequestDto.getIsActive(),
                employeeRequestDto.getRoles().stream().map(EmployeeRole::valueOf).collect(Collectors.toSet()));
    }
}

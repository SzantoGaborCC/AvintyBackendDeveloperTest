package com.jwt_test.app.service;

import com.jwt_test.app.dto.request.DepartmentRequestDto;
import com.jwt_test.app.dto.response.DepartmentResponseDto;
import com.jwt_test.app.entity.Department;
import com.jwt_test.app.entity.Employee;
import com.jwt_test.app.exception.DepartmentNotFoundException;
import com.jwt_test.app.exception.InvalidDepartmentIdInRequestBodyException;
import com.jwt_test.app.exception.InvalidEmployeeIdInRequestBodyException;
import com.jwt_test.app.repository.DepartmentRepository;
import com.jwt_test.app.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public DepartmentService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentRepository.getAllDepartments(DepartmentResponseDto.class);
    }

    public List<String> findNamesLikeNameOrderByName(String name) {
        return departmentRepository.findNamesLikeNameOrderByName(name);
    }

    public DepartmentResponseDto findById(Integer id) {
        return departmentRepository.findById(id, DepartmentResponseDto.class)
            .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    public void deleteById(Integer id) {
        departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id)).getEmployees().stream()
                .forEach(employee -> { //for each employee from that department, set department to null
                    employee.setDepartment(null);
                    employeeRepository.save(employee);
        });
        departmentRepository.deleteById(id);
    }

    public void add(DepartmentRequestDto departmentRequestDto) {
        Department department = createDepartmentFromDto(departmentRequestDto);
        if (department.getManager() != null) {
            department.getManager().setDepartment(department); //manager is an employee, too.
        }
        departmentRepository.save(department);
        saveDepartmentEmployees(departmentRequestDto, department);
    }

    public void update(int id, DepartmentRequestDto departmentRequestDto) {
        if (departmentRepository.existsById(id)) {
            Department department = createDepartmentFromDto(departmentRequestDto);
            department.setId(id); //set id from pathvariable
            if (department.getManager() != null) {
                department.getManager().setDepartment(department); //manager is an employee, too.
            }
            departmentRepository.save(department);
            saveDepartmentEmployees(departmentRequestDto, department);
        } else {
            throw new InvalidDepartmentIdInRequestBodyException(id);
        }
    }

    private Department createDepartmentFromDto(DepartmentRequestDto departmentRequestDto) {
        return new Department(
                departmentRequestDto.getName(),
                departmentRequestDto.getManagerId() == null ? null :
                        employeeRepository.findById(departmentRequestDto.getManagerId())
                                .orElseThrow(() -> new InvalidEmployeeIdInRequestBodyException(departmentRequestDto.getManagerId())));
    }

    private void saveDepartmentEmployees(DepartmentRequestDto departmentRequestDto, Department department) {
        departmentRequestDto.getEmployeeIds().stream()
                .forEach(employeeId -> { //set the department id for each found employee
                    Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new InvalidEmployeeIdInRequestBodyException(employeeId));
                    employee.setDepartment(department);
                    employeeRepository.save(employee);
                });
    }
}
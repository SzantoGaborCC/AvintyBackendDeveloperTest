package com.jwt_test.app.controller;

import com.jwt_test.app.dto.request.EmployeeRequestDto;
import com.jwt_test.app.dto.response.ServerMessageDto;
import com.jwt_test.app.dto.response.EmployeeResponseDto;
import com.jwt_test.app.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.jwt_test.app.utility.Utility.createBadRequestMessageResponseEntity;

@RestController
@RequestMapping("/employees")
@CrossOrigin(origins = "http://localhost:5000")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns all employees")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<EmployeeResponseDto> getAllEmployees() { return employeeService.getAllEmpoyees(); }

    @RequestMapping(value =  "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns an employee by id")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDto> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(employeeService.findById(id));
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Adds an employee to the database")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ServerMessageDto> add(
            @RequestBody @Valid EmployeeRequestDto employeeRequestDto,
            BindingResult result) {
        if (result.hasErrors()) {
            return createBadRequestMessageResponseEntity(result);
        } else {
            employeeService.add(employeeRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ServerMessageDto("Employee has been saved."));
        }
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Updates an employee by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServerMessageDto> update(
            @PathVariable("id") Integer id,
            @RequestBody @Valid EmployeeRequestDto employeeRequestDto,
            BindingResult result) {
        if (result.hasErrors()) {
            return createBadRequestMessageResponseEntity(result);
        } else {
            employeeService.update(id, employeeRequestDto);
            return ResponseEntity.ok().body(new ServerMessageDto("Employee has been updated."));
        }
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deletes an employee by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServerMessageDto> deleteById(@PathVariable("id") int id) {
        employeeService.deleteById(id);
        return ResponseEntity.ok().body(new ServerMessageDto("Employee has been deleted."));
    }
}

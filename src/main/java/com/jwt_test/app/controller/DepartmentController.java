package com.jwt_test.app.controller;

import com.jwt_test.app.dto.request.DepartmentRequestDto;
import com.jwt_test.app.dto.response.ServerMessageDto;
import com.jwt_test.app.dto.response.DepartmentResponseDto;
import com.jwt_test.app.service.DepartmentService;
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
@CrossOrigin(origins = "http://localhost:5000")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @RequestMapping(value =  "/dep-emp", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns all departments with employees")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<DepartmentResponseDto> getAllDepartmentsWithEmployeesIncluded() {
        return departmentService.getAllDepartments();
    }

    @RequestMapping(value =  "department/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns a department by id")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponseDto> findById(@PathVariable("id") Integer id) {
            return ResponseEntity.ok().body(departmentService.findById(id));
    }

    @RequestMapping(value =  "/department", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns departments with similar names to name parameter")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<String> findNamesLikeNameOrderByName(@RequestParam(value = "name", required = false) String name) {
        return departmentService.findNamesLikeNameOrderByName(name);
    }

    @RequestMapping(value =  "/department", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Adds a department to the database")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServerMessageDto> add(
            @RequestBody @Valid DepartmentRequestDto departmentRequestDto,
            BindingResult result) {
        if (result.hasErrors()) {
            return createBadRequestMessageResponseEntity(result);
        } else {
            departmentService.add(departmentRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ServerMessageDto("Department has been created."));
        }
    }

    @RequestMapping(value = "/department/{id}" , method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Updates a department by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServerMessageDto> update(
            @PathVariable("id") int id, @RequestBody @Valid DepartmentRequestDto departmentRequestDto, BindingResult result) {
        if (result.hasErrors()) {
            return createBadRequestMessageResponseEntity(result);
        } else {
            departmentService.update(id, departmentRequestDto);
            return ResponseEntity.ok().body(new ServerMessageDto("Department has been updated."));
        }
    }

    @RequestMapping(value = "department/{id}" , method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deletes a department by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServerMessageDto> deleteById(@PathVariable("id") Integer id) {
        departmentService.deleteById(id);
        return ResponseEntity.ok(new ServerMessageDto("Department has been deleted"));
    }
}

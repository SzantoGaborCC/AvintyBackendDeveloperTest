package com.jwt_test.app.integration_tests;

import com.jwt_test.app.dto.request.DepartmentRequestDto;
import com.jwt_test.app.dto.request.EmployeeRequestDto;
import com.jwt_test.app.dto.response.ServerMessageDto;
import com.jwt_test.app.entity.Department;
import com.jwt_test.app.entity.Employee;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("NO_AUTH")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class EmployeeTests {
    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int serverPort;

    @Value("${server.servlet.context-path}")
    private String servletContextPath;

    private String employeesUrl;
    private String departmentUrl;

    private DepartmentRequestDto HR = new DepartmentRequestDto("Human Resources", null);
    private DepartmentRequestDto RS = new DepartmentRequestDto("Research", null);
    private DepartmentRequestDto DEVS = new DepartmentRequestDto("Development", null);

    private EmployeeRequestDto JOHN = new EmployeeRequestDto("johndoe@gmail.com",
            "abcd1234", "John Doe", null, true, List.of("USER", "ADMIN"));
    private EmployeeRequestDto MARY = new EmployeeRequestDto("mary33@gmail.com",
            "pass2222", "Mary Poppins", null, true, List.of("ADMIN"));
    private EmployeeRequestDto SCOTT = new EmployeeRequestDto("sfitzgerald@gmail.com",
            "password", "Scott Fitzgerald", null, true, List.of("USER"));

    private EmployeeRequestDto MARTIN = new EmployeeRequestDto("msmith@gmail.com",
            "hkak1111", "Martin Smith", null, true, List.of("USER"));
    private EmployeeRequestDto JENNY = new EmployeeRequestDto("jennythefunny@gmail.com",
            "pass7777", "Jenny Beecham", null, true, List.of("USER"));
    private EmployeeRequestDto FELIX = new EmployeeRequestDto("sulla@gmail.com",
            "spqrpwnage", "Lucius Cornelius Sulla", null, true, List.of("USER"));

    @BeforeAll
    public void beforeAll() {
        employeesUrl = "http://localhost:" + serverPort + servletContextPath + "/employees";
        departmentUrl = "http://localhost:" + serverPort + servletContextPath + "/department";
    }

    @Test
    @Order(1)
    void emptyDatabase_getAllEmployees_shouldReturnEmptyList() {
        assertEquals(Collections.emptyList(), List.of(testRestTemplate.getForObject(employeesUrl, Employee[].class)));
    }

    @Test
    @Order(2)
    void emptyDatabase_addOne_shouldReturnAddedEmployeeWithNameAndRoles() {
        testRestTemplate.postForLocation(employeesUrl, JOHN);
        Employee result = testRestTemplate.getForObject(employeesUrl + "/" + 1, Employee.class);
        assertEquals(JOHN.getFullName(), result.getFullName());
    }

    @Test
    @Order(3)
    void deleteOne_getAll_shouldEmptyEmployeeTable() {
        testRestTemplate.delete(employeesUrl + "/" + 1);
        Employee[] remainingData = testRestTemplate.getForObject(employeesUrl, Employee[].class);
        assertEquals(0, remainingData.length);
    }

    @Test
    @Order(4)
    void moreEmployeesStored_getAll_shouldReturnThem() {
        List<EmployeeRequestDto> testData = List.of(JOHN, MARY, SCOTT);
        testData.forEach(employee -> testRestTemplate.postForLocation(employeesUrl, employee));

        Employee[] result = testRestTemplate.getForObject(employeesUrl,Employee[].class);
        assertEquals(3, result.length);

        Set<String> employeeFullNames = testData.stream().map(EmployeeRequestDto::getFullName).collect(Collectors.toSet());
        assertTrue(Arrays.stream(result).map(Employee::getFullName).allMatch(employeeFullNames::contains));
    }

    @Test
    @Order(5)
    void updateSecond_shouldMakeExpectedChanges() {
        testRestTemplate.put(employeesUrl + "/" + 2, MARTIN);
        Employee result = testRestTemplate.getForObject(employeesUrl + "/" + 2, Employee.class);
        assertEquals(MARTIN.getFullName(), result.getFullName());
    }

    @Test
    @Order(6)
    void postADepartment_withEmployees_ifDepartmentDeleteById_EmployeesShouldNotBeDeleted() {
        Employee[] employees = testRestTemplate.getForObject(employeesUrl,Employee[].class);
        HR.setManagerId(employees[0].getId());
        HR.getEmployeeIds().add(employees[1].getId());
        testRestTemplate.postForLocation(departmentUrl, HR);
        Department testDepartment = testRestTemplate.getForObject(departmentUrl + "/" + 1, Department.class);
        assertEquals(2, testDepartment.getEmployees().size()); //do we actually have both of them as employees?!
        testRestTemplate.delete(departmentUrl + "/" + testDepartment.getId());
        Employee[] remainingEmployees = testRestTemplate.getForObject(employeesUrl, Employee[].class);
        assertEquals(employees.length, remainingEmployees.length);
    }

    @Test
    @Order(7)
    void ifManagerDeleteById_DepartmentShouldReflectChange() {
        Employee testManager = testRestTemplate.getForObject(employeesUrl + "/" + 1, Employee.class);
        HR.setManagerId(testManager.getId());
        testRestTemplate.postForLocation(departmentUrl, HR);
        testRestTemplate.delete(employeesUrl + "/" + testManager.getId());
        Department testDepartment = testRestTemplate.getForObject(departmentUrl + "/" + 1, Department.class);
        assertNull(testDepartment.getManager());
    }

    @Test
    @Order(8)
    void oneEmployeeStored_updateWithWrongId_EmployeeShouldBeUnchanged_ReturningBadRequest() {
        Employee testEmployee = testRestTemplate.getForObject(employeesUrl + "/" + 2, Employee.class);
        String originalName = testEmployee.getFullName();
        assertNotNull(testEmployee.getFullName());
        testEmployee.setFullName(originalName + "update");
        ResponseEntity<ServerMessageDto> resp = testRestTemplate.exchange(
                employeesUrl + "/" + 42, HttpMethod.PUT, new HttpEntity<>(testEmployee, null), ServerMessageDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Employee result = testRestTemplate.getForObject(employeesUrl + "/" + 2, Employee.class);
        assertEquals(originalName, result.getFullName());
    }

    @Test
    void getOneByWrongId_shouldRespond404() {
        ResponseEntity<Employee> response = testRestTemplate.getForEntity(employeesUrl + "/12345", Employee.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void postEmployeeWithInvalidEmail_shouldRespond400() {
        ResponseEntity<ServerMessageDto> response = testRestTemplate.postForEntity(
                employeesUrl, new EmployeeRequestDto(
                        "fdf&fdfd.de", "dffdfdfdf", "Kang the Pawner", null, true,
                        List.of("USER")), ServerMessageDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postInvalidEmployeeWithTooShortPassword_shouldRespond400() {
        ResponseEntity<ServerMessageDto> response = testRestTemplate.postForEntity(
                employeesUrl, new EmployeeRequestDto(
                        "fdf@fdfd.de", "fl", "Kang the Pawner", null, true,
                        List.of("USER")), ServerMessageDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postInvalidEmployeeWithInvalidFullName_shouldRespond400() {
        ResponseEntity<ServerMessageDto> response = testRestTemplate.postForEntity(
                employeesUrl, new EmployeeRequestDto(
                        "fdf@fdfd.de", "flfdfddffdf", "            ", null, true,
                        List.of("USER")), ServerMessageDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postInvalidEmployeeWithInvalidDepartment_shouldRespond422() {
        ResponseEntity<ServerMessageDto> response =testRestTemplate.postForEntity(
                employeesUrl, new EmployeeRequestDto(
                        "fdf@fdfd.de", "flfdfddffdf", "Kang the Pawner", 44, true,
                        List.of("USER")), ServerMessageDto.class);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }
}

package com.jwt_test.app.integration_tests;

import com.jwt_test.app.dto.request.DepartmentRequestDto;
import com.jwt_test.app.dto.request.EmployeeRequestDto;
import com.jwt_test.app.dto.response.ServerMessageDto;
import com.jwt_test.app.dto.response.DepartmentResponseDto;
import com.jwt_test.app.dto.response.EmployeeIdAndFullNameResponseDto;
import com.jwt_test.app.dto.response.EmployeeResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("NO_AUTH")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class DepartmentTests {
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
    private String getAllDepartmentsUrl;

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
        getAllDepartmentsUrl = "http://localhost:" + serverPort + servletContextPath + "/dep-emp";
    }

    @Test
    @Order(1)
    void emptyDatabase_getAllDepartments_shouldReturnEmptyList() {
        assertEquals(Collections.emptyList(), List.of(testRestTemplate.getForObject(
                getAllDepartmentsUrl, DepartmentResponseDto[].class)));
    }

    @Test
    @Order(2)
    void emptyDatabase_addOne_shouldReturnAddedDepartment() {
        testRestTemplate.postForLocation(departmentUrl, HR);
        DepartmentResponseDto result = testRestTemplate.getForObject(departmentUrl + "/" + 1, DepartmentResponseDto.class);
        assertEquals(HR.getName(), result.getName());
    }

    @Test
    @Order(3)
    void deleteOne_getAll_shouldEmptyDepartmentTable() {
        testRestTemplate.delete(departmentUrl + "/" + 1);
        DepartmentResponseDto[] remainingData = testRestTemplate.getForObject(getAllDepartmentsUrl, DepartmentResponseDto[].class);
        assertEquals(0, remainingData.length);
    }

    @Test
    @Order(4)
    void someDepartmentsStored_getAll_shouldReturnAll() {
        List<DepartmentRequestDto> testData = List.of(HR, RS, DEVS);
        testData.forEach(department -> testRestTemplate.postForLocation(departmentUrl, department));

        DepartmentResponseDto[] result = testRestTemplate.getForObject(getAllDepartmentsUrl, DepartmentResponseDto[].class);
        assertEquals(testData.size(), result.length);

        Set<String> departmentNames = testData.stream().map(DepartmentRequestDto::getName).collect(Collectors.toSet());
        assertTrue(Arrays.stream(result).map(DepartmentResponseDto::getName).allMatch(departmentNames::contains));
    }

    @Test
    @Order(5)
    void updateSecond_shouldMakeExpectedChanges() {
        RS.setName("Research updated");
        testRestTemplate.put(departmentUrl + "/" + 3, RS);
        DepartmentResponseDto result = testRestTemplate.getForObject(departmentUrl + "/" + 3, DepartmentResponseDto.class);
        assertEquals("Research updated", result.getName());
    }

    @Test
    @Order(6)
    void ifDepartmentPutWithEmployeeIds_EmployeesShouldHaveThatDepartmentSet() {
        List<EmployeeRequestDto> testData = List.of(JOHN, MARY, SCOTT);
        testData.forEach(employee -> testRestTemplate.postForLocation(employeesUrl, employee));

        HR.setEmployeeIds(List.of(1, 2, 3));
        testRestTemplate.put(departmentUrl + "/" + 2, HR);

        Set<EmployeeIdAndFullNameResponseDto> result = testRestTemplate.getForObject(departmentUrl + "/" + 2, DepartmentResponseDto.class).getEmployees();

        Set<String> employeeFullNames = testData.stream().map(EmployeeRequestDto::getFullName).collect(Collectors.toSet());
        assertTrue(result.stream().map(EmployeeIdAndFullNameResponseDto::getFullName).allMatch(employeeFullNames::contains));
    }

    @Test
    @Order(7)
    void ifDepartmentDeleted_employeesShouldHaveThatDepartmentRemoved() {
        Set<Integer> departmentEmployeeIds = testRestTemplate.getForObject(departmentUrl + "/" + 2, DepartmentResponseDto.class)
                .getEmployees().stream().map(EmployeeIdAndFullNameResponseDto::getId).collect(Collectors.toSet());
        testRestTemplate.delete(departmentUrl + "/" + 2);
        Set<EmployeeResponseDto> employeesShouldBeWithoutADepartment = new HashSet<>();
        departmentEmployeeIds.forEach(id ->
                employeesShouldBeWithoutADepartment.add(testRestTemplate.getForObject(employeesUrl + "/" + id, EmployeeResponseDto.class)));
        assertTrue(employeesShouldBeWithoutADepartment.stream().allMatch(employee -> employee.getDepartment() == null));
    }

    @Test
    @Order(8)
    void getOneByWrongId_shouldRespond404() {
        ResponseEntity<DepartmentResponseDto> response = testRestTemplate.getForEntity(departmentUrl + "/12345", DepartmentResponseDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(9)
    void postInvalidDepartmentWithInvalidName_shouldRespond400() {
        ResponseEntity<ServerMessageDto> response = testRestTemplate.postForEntity(
                departmentUrl, new DepartmentRequestDto("            ", null), ServerMessageDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(10)
    void postInvalidDepartmentWithInvalidManager_shouldRespond422() {
        ResponseEntity<ServerMessageDto> response = testRestTemplate.postForEntity(
                departmentUrl, new DepartmentRequestDto("Catering", 42), ServerMessageDto.class);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void someDepartmentsStored_getByNameLike_shouldReturnMatchingOnes() {
        String testParameter = "res";
        List<String> expectedNames = List.of("Research updated");
        String[] result = testRestTemplate.getForObject(
                departmentUrl + "?name=" + testParameter, String[].class);
        DepartmentResponseDto[] all = testRestTemplate.getForObject(
                getAllDepartmentsUrl, DepartmentResponseDto[].class);
        assertEquals(1, result.length);
        assertTrue(Arrays.stream(result).allMatch(expectedNames::contains));
    }
}

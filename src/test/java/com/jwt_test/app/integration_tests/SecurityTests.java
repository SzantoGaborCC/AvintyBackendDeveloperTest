package com.jwt_test.app.integration_tests;

import com.jwt_test.app.dto.request.DepartmentRequestDto;
import com.jwt_test.app.dto.request.EmployeeRequestDto;
import com.jwt_test.app.entity.Employee;
import com.jwt_test.app.entity.EmployeeRole;
import com.jwt_test.app.repository.EmployeeRepository;
import com.jwt_test.app.security.EmployeeDetails;
import com.jwt_test.app.security.JwtUtils;
import com.jwt_test.app.security.dto.response.JwtLoginResponseDto;
import com.jwt_test.app.security.dto.request.JwtRefreshRequestDto;
import com.jwt_test.app.security.dto.response.JwtRefreshResponseDto;
import com.jwt_test.app.security.dto.request.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("WITH_AUTH")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
class SecurityTests {
    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    JwtUtils jwtUtils;

    @LocalServerPort
    private int serverPort;

    @Value("${server.servlet.context-path}")
    private String servletContextPath;

    private String employeesUrl;
    private String departmentUrl;

    private String getAllDepartmentsUrl;
    private String loginUrl;

    private String refreshTokenUrl;

    private HttpEntity<Void> httpEntity;

    private HttpHeaders httpHeaders = new HttpHeaders();

    private ObjectMapper objectMapper = new ObjectMapper();

    private JwtLoginResponseDto testJwtLoginResponseDto;

    private Employee adminGuy = new Employee(
            "adminguy@gmail.com",
            "$2a$10$mI2hgZb4oP90n5dIPIVQi.Lx28CqCvLcCCo1w24HR5ef5KiX0WUKy",
            "John Doe",
            null,
            true,
            Set.of(EmployeeRole.ADMIN)
    );

    private Employee userGuy = new Employee(
            "userguy@gmail.com",
            "$2a$12$l7OnlchvZw0GL061BHoCl.8ODJHOLIbEibL4Mxa/WkmJDrEv/N6a6",
            "John Doe",
            null,
            true,
            Set.of(EmployeeRole.USER)
    );

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
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        employeesUrl = "http://localhost:" + serverPort + "/employees";
        getAllDepartmentsUrl = "http://localhost:" + serverPort + "/dep-emp";
        departmentUrl = "http://localhost:" + serverPort + "/department";
        loginUrl = "http://localhost:" + serverPort + "/security/login";
        refreshTokenUrl = "http://localhost:" + serverPort + "/security/refreshtoken";
    }

    @Test
    @Order(1)
    public void simpleRequest_withoutValidBearerToken_shouldBeUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(employeesUrl)).andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    public void usingProperlyGeneratedAdmin_toGetJwt_shouldAllowAccess() throws Exception {
        employeeRepository.save(adminGuy);
        String token = jwtUtils.generateJwt(
                new EmployeeDetails(
                        employeeRepository.findById(1).orElseThrow()));
        assertNotNull(token);
        mockMvc.perform(MockMvcRequestBuilders.get(getAllDepartmentsUrl)
                .header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void usingValidCredentials_toLogin_shouldReturnValidJsonLoginResponse() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "adminguy@gmail.com",
                "security"
        );
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(loginUrl)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk()).andReturn();
        testJwtLoginResponseDto =
                objectMapper.readValue(result.getResponse().getContentAsString(), JwtLoginResponseDto.class);
        assertEquals("adminguy@gmail.com", testJwtLoginResponseDto.getEmail());
    }

    @Test
    @Order(4)
    public void usingLoginRequestDtoFromPreviousTest_shouldAllowAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(getAllDepartmentsUrl)
                .header("Authorization", "Bearer " + testJwtLoginResponseDto.getToken()))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void usingLoginRequestDtoFromPreviousTest_shouldGiveRefreshToken_thatCanRequestNewAndValidJwt() throws Exception {
        JwtRefreshRequestDto jwtRefreshRequestDto = new JwtRefreshRequestDto(testJwtLoginResponseDto.getRefreshToken());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(refreshTokenUrl)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(jwtRefreshRequestDto)))
                .andExpect(status().isOk()).andReturn();
        JwtRefreshResponseDto jwtRefreshResponseDto =
                objectMapper.readValue(result.getResponse().getContentAsString(), JwtRefreshResponseDto.class);
        assertNotNull(jwtRefreshResponseDto.getAccessToken());
        mockMvc.perform(MockMvcRequestBuilders.get(getAllDepartmentsUrl)
                        .header("Authorization", "Bearer " + jwtRefreshResponseDto.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void usingInvalidRefreshToken_shouldBeForbidden() throws Exception {
        JwtRefreshRequestDto jwtRefreshRequestDto = new JwtRefreshRequestDto("nowaythisworkscmon!");
        mockMvc.perform(MockMvcRequestBuilders
                        .post(refreshTokenUrl)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(jwtRefreshRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(6)
    public void usingInvalidCredentials_toLogin_shouldBeUnauthorized() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "adminguess@ultratech.com",
                "password12345"
        );
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(loginUrl)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    public void usingValidCredentialsWithOnlyUserAuthorization_toLogin_shouldRefuseAccessToAdminOperation() throws Exception {
        employeeRepository.save(userGuy);
        LoginRequestDto userLoginRequestDto = new LoginRequestDto(
                "userguy@gmail.com",
                "jelszo"
        );
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(loginUrl)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(userLoginRequestDto)))
                .andExpect(status().isOk()).andReturn();
        JwtLoginResponseDto userJwtLoginResponseDto =
                objectMapper.readValue(result.getResponse().getContentAsString(), JwtLoginResponseDto.class);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(employeesUrl)
                        .header("Authorization", "Bearer " + userJwtLoginResponseDto.getToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(JOHN)))
                .andExpect(status().isForbidden());
    }
}

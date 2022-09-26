package com.jwt_test.app.integration_tests;

import com.jwt_test.app.entity.Department;
import com.jwt_test.app.entity.Employee;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.UnknownContentTypeException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("NO_AUTH")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CorsTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int serverPort;

    @Value("${server.servlet.context-path}")
    private String servletContextPath;

    @Value("${cors.origin}")
    private String corsOrigin;

    @Value("${cors.port}")
    private String corsPort;

    private String employeesUrl;
    private String departmentUrl;

    private String getAllDepartmentsUrl;

    private HttpEntity<Void> httpEntity;

    private HttpHeaders httpHeaders;

    @BeforeAll
    public void setUp() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        employeesUrl = "http://localhost:" + serverPort + servletContextPath + "/employees";
        departmentUrl = "http://localhost:" + serverPort + servletContextPath + "/department";
        getAllDepartmentsUrl = "http://localhost:" + serverPort + servletContextPath + "/dep-emp";
        httpHeaders = new HttpHeaders();
    }

    @Test
    @Order(1)
    void shouldAllowAccessToDepartment_whenOriginIsSet_theRightValue() {
        httpHeaders.setOrigin(corsOrigin + ":" + corsPort); //set origin to the right value
        httpEntity = new HttpEntity<>(httpHeaders);
        assertEquals(Collections.emptyList(), List.of(
                restTemplate.exchange(getAllDepartmentsUrl, HttpMethod.GET, httpEntity, Department[].class).getBody()));
    }

    @Test
    @Order(2)
    void shouldThrowWhenAccessToDepartment_whenOriginIsSet_toWrongValue() {
        httpHeaders.setOrigin("http://www.hacker.com");
        httpEntity = new HttpEntity<>(httpHeaders);
        assertThrows(UnknownContentTypeException.class,
                () -> restTemplate.exchange(getAllDepartmentsUrl, HttpMethod.GET, httpEntity, Department[].class));
    }

    @Test
    @Order(3)
    void shouldAllowAccessToEmployee_whenOriginIsSet_theRightValue() {
        httpHeaders.setOrigin(corsOrigin + ":" + corsPort); //set origin to the right value
        httpEntity = new HttpEntity<>(httpHeaders);
        assertEquals(Collections.emptyList(), List.of(
                restTemplate.exchange(employeesUrl, HttpMethod.GET, httpEntity, Employee[].class).getBody()));
    }

    @Test
    @Order(4)
    void shouldThrowWhenAccessToEmployee_whenOriginIsSet_toWrongValue() {
        httpHeaders.setOrigin("http://www.hacker.com");
        httpEntity = new HttpEntity<>(httpHeaders);
        assertThrows(UnknownContentTypeException.class,
                () -> restTemplate.exchange(employeesUrl, HttpMethod.GET, httpEntity, Employee[].class));
    }
}

package com.jwt_test.app.security.entity;

import com.jwt_test.app.entity.Employee;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}

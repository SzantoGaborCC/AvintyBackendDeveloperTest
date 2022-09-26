package com.jwt_test.app.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
public class Department extends BaseEntity {
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @OneToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST)
    private Set<Employee> employees;

    public Department(String name, Employee manager) {
        this.name = name;
        this.manager = manager;
    }
}

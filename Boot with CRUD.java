
<dependencies>
    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- H2 Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Spring Boot Starter Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String department;
    private double salary;

    // Getters and Setters
}

package com.example.demo.repository;

import com.example.demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    public Employee save(Employee employee) {
        return repository.save(employee);
    }

    public List<Employee> getAll() {
        return repository.findAll();
    }

    public Optional<Employee> getById(Long id) {
        return repository.findById(id);
    }

    public Employee update(Long id, Employee newEmployee) {
        return repository.findById(id).map(emp -> {
            emp.setName(newEmployee.getName());
            emp.setDepartment(newEmployee.getDepartment());
            emp.setSalary(newEmployee.getSalary());
            return repository.save(emp);
        }).orElseThrow();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    public Employee save(Employee employee) {
        return repository.save(employee);
    }

    public List<Employee> getAll() {
        return repository.findAll();
    }

    public Optional<Employee> getById(Long id) {
        return repository.findById(id);
    }

    public Employee update(Long id, Employee newEmployee) {
        return repository.findById(id).map(emp -> {
            emp.setName(newEmployee.getName());
            emp.setDepartment(newEmployee.getDepartment());
            emp.setSalary(newEmployee.getSalary());
            return repository.save(emp);
        }).orElseThrow();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @PostMapping
    public Employee create(@RequestBody Employee emp) {
        return service.save(emp);
    }

    @GetMapping
    public List<Employee> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable Long id) {
        return service.getById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @RequestBody Employee emp) {
        return service.update(id, emp);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrudApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrudApplication.class, args);
    }
}

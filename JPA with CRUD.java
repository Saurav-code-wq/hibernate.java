
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>jpa-crud</artifactId>
  <version>1.0</version>

  <dependencies>
    <!-- JPA API -->
    <dependency>
      <groupId>javax.persistence</groupId>
      <artifactId>javax.persistence-api</artifactId>
      <version>2.2</version>
    </dependency>

    <!-- Hibernate JPA Implementation -->
    <dependency>
      <groupId>org.hibernate</groupId>
      <artifactId>hibernate-core</artifactId>
      <version>5.6.15.Final</version>
    </dependency>

    <!-- MySQL Connector -->
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.33</version>
    </dependency>
  </dependencies>
</project>

<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
  xmlns="http://xmlns.jcp.org/xml/ns/persistence"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
                      http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
  
  <persistence-unit name="EmployeePU">
    <class>com.example.Employee</class>
    <properties>
      <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
      <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/testdb"/>
      <property name="javax.persistence.jdbc.user" value="root"/>
      <property name="javax.persistence.jdbc.password" value="password"/>

      <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
      <property name="hibernate.hbm2ddl.auto" value="update"/>
      <property name="hibernate.show_sql" value="true"/>
    </properties>
  </persistence-unit>
</persistence>

package com.example;

import javax.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String department;
    private double salary;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}

package com.example;

import javax.persistence.*;

public class EmployeeService {

    private EntityManagerFactory emf;

    public EmployeeService() {
        emf = Persistence.createEntityManagerFactory("EmployeePU");
    }

    public void create(Employee emp) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(emp);
        em.getTransaction().commit();
        em.close();
    }

    public Employee read(int id) {
        EntityManager em = emf.createEntityManager();
        Employee emp = em.find(Employee.class, id);
        em.close();
        return emp;
    }

    public void update(Employee emp) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(emp);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        Employee emp = em.find(Employee.class, id);
        if (emp != null) {
            em.getTransaction().begin();
            em.remove(emp);
            em.getTransaction().commit();
        }
        em.close();
    }

    public void close() {
        emf.close();
    }
}

package com.example;

public class Main {
    public static void main(String[] args) {
        EmployeeService service = new EmployeeService();

        // CREATE
        Employee emp = new Employee();
        emp.setName("Bob");
        emp.setDepartment("HR");
        emp.setSalary(40000.0);
        service.create(emp);

        // READ
        Employee e = service.read(emp.getId());
        System.out.println("Read: " + e.getName() + ", " + e.getDepartment());

        // UPDATE
        e.setSalary(45000.0);
        service.update(e);

        // DELETE
        service.delete(e.getId());

        service.close();
    }
}

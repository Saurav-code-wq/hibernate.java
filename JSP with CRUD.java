
CREATE DATABASE testdb;
USE testdb;

CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    department VARCHAR(100),
    salary DOUBLE
);

<web-app>
    <servlet>
        <servlet-name>EmployeeServlet</servlet-name>
        <servlet-class>com.example.crud.EmployeeServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>EmployeeServlet</servlet-name>
        <url-pattern>/EmployeeServlet</url-pattern>
    </servlet-mapping>
</web-app>

package com.example.crud;

public class Employee {
    private int id;
    private String name;
    private String department;
    private double salary;

    // Getters and Setters
}

package com.example.crud;

import java.sql.*;
import java.util.*;

public class EmployeeDao {
    private String jdbcURL = "jdbc:mysql://localhost:3306/testdb";
    private String jdbcUsername = "root";
    private String jdbcPassword = "password";

    private static final String INSERT_EMPLOYEE = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_EMPLOYEES = "SELECT * FROM employees";
    private static final String SELECT_EMPLOYEE_BY_ID = "SELECT * FROM employees WHERE id = ?";
    private static final String UPDATE_EMPLOYEE = "UPDATE employees SET name = ?, department = ?, salary = ? WHERE id = ?";
    private static final String DELETE_EMPLOYEE = "DELETE FROM employees WHERE id = ?";

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    public void insertEmployee(Employee emp) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_EMPLOYEE)) {
            stmt.setString(1, emp.getName());
            stmt.setString(2, emp.getDepartment());
            stmt.setDouble(3, emp.getSalary());
            stmt.executeUpdate();
        }
    }

    public List<Employee> selectAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_EMPLOYEES)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("id"));
                e.setName(rs.getString("name"));
                e.setDepartment(rs.getString("department"));
                e.setSalary(rs.getDouble("salary"));
                employees.add(e);
            }
        }
        return employees;
    }

    public Employee selectEmployee(int id) throws SQLException {
        Employee emp = null;
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_EMPLOYEE_BY_ID)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setName(rs.getString("name"));
                emp.setDepartment(rs.getString("department"));
                emp.setSalary(rs.getDouble("salary"));
            }
        }
        return emp;
    }

    public boolean updateEmployee(Employee emp) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_EMPLOYEE)) {
            stmt.setString(1, emp.getName());
            stmt.setString(2, emp.getDepartment());
            stmt.setDouble(3, emp.getSalary());
            stmt.setInt(4, emp.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteEmployee(int id) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_EMPLOYEE)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}

package com.example.crud;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class EmployeeServlet extends HttpServlet {
    private EmployeeDao dao = new EmployeeDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                List<Employee> list = dao.selectAllEmployees();
                request.setAttribute("list", list);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            } else if (action.equals("edit")) {
                int id = Integer.parseInt(request.getParameter("id"));
                Employee emp = dao.selectEmployee(id);
                request.setAttribute("emp", emp);
                request.getRequestDispatcher("edit-employee.jsp").forward(request, response);
            } else if (action.equals("delete")) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.deleteEmployee(id);
                response.sendRedirect("EmployeeServlet");
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = request.getParameter("id") != null ? Integer.parseInt(request.getParameter("id")) : 0;
            String name = request.getParameter("name");
            String department = request.getParameter("department");
            double salary = Double.parseDouble(request.getParameter("salary"));

            Employee emp = new Employee();
            emp.setName(name);
            emp.setDepartment(department);
            emp.setSalary(salary);
            emp.setId(id);

            if (id == 0) {
                dao.insertEmployee(emp);
            } else {
                dao.updateEmployee(emp);
            }

            response.sendRedirect("EmployeeServlet");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

<%@ page import="java.util.*, com.example.crud.Employee" %>
<html>
<body>
    <h2>Employee List</h2>
    <a href="add-employee.jsp">Add Employee</a>
    <table border="1">
        <tr><th>ID</th><th>Name</th><th>Department</th><th>Salary</th><th>Actions</th></tr>
        <%
            List<Employee> list = (List<Employee>) request.getAttribute("list");
            for (Employee e : list) {
        %>
        <tr>
            <td><%= e.getId() %></td>
            <td><%= e.getName() %></td>
            <td><%= e.getDepartment() %></td>
            <td><%= e.getSalary() %></td>
            <td>
                <a href="EmployeeServlet?action=edit&id=<%= e.getId() %>">Edit</a> |
                <a href="EmployeeServlet?action=delete&id=<%= e.getId() %>">Delete</a>
            </td>
        </tr>
        <% } %>
    </table>
</body>
</html>



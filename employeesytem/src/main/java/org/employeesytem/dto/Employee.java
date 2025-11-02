package org.employeesytem.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class Employee {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String department;
    private final BigDecimal salary;

    public Employee(int id, String firstName, String lastName, String email, String department, BigDecimal salary) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id &&
                Objects.equals(firstName, employee.firstName) &&
                Objects.equals(lastName, employee.lastName) &&
                Objects.equals(email, employee.email) &&
                Objects.equals(department, employee.department) &&
                Objects.equals(salary, employee.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, department, salary);
    }
}

package org.employeesytem.repository;

import org.employeesytem.dto.Employee;

import java.util.List;

public interface EmployeeRepository {
    List<Employee> findAll();
}

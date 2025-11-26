package org.employeesytem.repository;

import org.employeesytem.dto.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    List<Employee> findAll();

    Employee save(Employee employee);

    Optional<Employee> findById(int id);

    void deleteById(int id);
}

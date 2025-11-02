package org.employeesytem.service;

import org.employeesytem.dto.Employee;
import org.employeesytem.repository.EmployeeRepositoryJDBCImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepositoryJDBCImpl repository;

    public EmployeeService(EmployeeRepositoryJDBCImpl repository) {
        this.repository = repository;
    }

    public List<Employee> findAllEmployees() {
        return repository.findAll();
    }
}

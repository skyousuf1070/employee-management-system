package org.employeesytem.service;

import org.employeesytem.dto.Employee;
import org.employeesytem.exceptions.DuplicateEmployeeException;
import org.employeesytem.exceptions.EmployeeNotFoundException;
import org.employeesytem.repository.EmployeeRepositoryJDBCImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepositoryJDBCImpl repository;

    public EmployeeService(EmployeeRepositoryJDBCImpl repository) {
        this.repository = repository;
    }

    public List<Employee> findAllEmployees() {
        return repository.findAll();
    }

    public Employee addEmployee(Employee employee) {
        try {
            return repository.save(employee);
        } catch (IllegalArgumentException exception) {
            throw new DuplicateEmployeeException(exception.getMessage());
        }
    }

    public Employee findByEmployeeId(int employeeId) {
        Optional<Employee> employee = repository.findById(employeeId);
        if (employee.isPresent()) {
            return employee.get();
        } else {
            throw new EmployeeNotFoundException("Employee with id " + employeeId + " not exists");
        }
    }

    public Employee updateEmployee(int id, Employee employee) {
        return repository.update(id, employee);
    }
}

package org.employeesytem.service;

import org.employeesytem.dto.Employee;
import org.employeesytem.exceptions.DuplicateEmployeeException;
import org.employeesytem.exceptions.EmployeeNotFoundException;
import org.employeesytem.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Page<Employee> findAllEmployees(Pageable pageable) {
        return repository.findAll(pageable);
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
        employee.setId(id);
        return repository.save(employee);
    }

    public void deleteEmployee(int employeeId) {
        repository.deleteById(employeeId);
    }

    public Long getEmployeeCount() {
        return repository.count();
    }
}

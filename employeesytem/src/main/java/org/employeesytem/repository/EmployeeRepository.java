package org.employeesytem.repository;

import org.employeesytem.dto.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    Page<Employee> findAll(Pageable pageable);

    Employee save(Employee employee);

    Optional<Employee> findById(int id);

    void deleteById(int id);

    Long count();
}

package org.employeesytem.repository.jdbc;

import org.employeesytem.dto.Employee;
import org.employeesytem.exceptions.EmployeeNotFoundException;
import org.employeesytem.repository.EmployeeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class EmployeeRepositoryJDBCImpl implements EmployeeRepository {
    private final JdbcTemplate jdbc;

    public EmployeeRepositoryJDBCImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // To-do: Implement Pagination in JDBC
    @Override
    public Page<Employee> findAll(Pageable pageable) {
        String sql = "SELECT * FROM employee";
        jdbc.query(sql, new BeanPropertyRowMapper<>(Employee.class));
        return new PageImpl<>(Collections.emptyList());
    }

    public Employee save(Employee employee) {
        int rows = update(employee);
        if (rows > 0) {
            return employee;
        }
        return insert(employee);
    }

    private Employee insert(Employee employee) {
        try {
            String sql = "INSERT INTO employee (id, first_name, last_name, email, department, salary) VALUES (?, ?, ?, ?, ?, ?)";
            jdbc.update(sql,
                    employee.getId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmail(),
                    employee.getDepartment(),
                    employee.getSalary());
            return employee;
        } catch (DuplicateKeyException exception) {
            throw new IllegalArgumentException("Employee with id " + employee.getId() + " already exists", exception);
        }
    }

    public Optional<Employee> findById(int id) {
        String sql = "SELECT * FROM employee WHERE id = ?";
        List<Employee> employees = jdbc.query(sql,
                new BeanPropertyRowMapper<>(Employee.class), id);
        return employees.isEmpty() ? Optional.empty() : Optional.of(employees.get(0));
    }

    private int update(Employee employee) {
        String sql = "UPDATE employee SET first_name = ?, last_name = ?, email = ?, department = ?, salary = ? WHERE id = ?";
        return jdbc.update(sql,
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getId());
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM employee WHERE id = ?";
        int rows = jdbc.update(sql, id);
        if (rows == 0) {
            throw new EmployeeNotFoundException("Employee with ID " + id + " not found");
        }
    }

    @Override
    public Long count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM employee", Long.class);
    }

    // To-do: Implement this
    @Override
    public Page<Employee> findByCriteria(String name, String department, Pageable pageable) {
        return null;
    }
}

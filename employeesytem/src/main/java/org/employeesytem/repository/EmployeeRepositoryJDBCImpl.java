package org.employeesytem.repository;

import org.employeesytem.dto.Employee;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepositoryJDBCImpl implements EmployeeRepository {
    private final JdbcTemplate jdbc;

    public EmployeeRepositoryJDBCImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT * FROM employee";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Employee.class));
    }

    public Employee save(Employee employee) {
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
}

package org.employeesytem.repository;

import org.employeesytem.dto.Employee;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepositoryJDBCImpl implements EmployeeRepository{
    private final JdbcTemplate jdbc;

    public EmployeeRepositoryJDBCImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT * FROM employee";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Employee.class));
    }
}

package org.employeesytem.repository;

import org.employeesytem.dto.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class EmployeeRepositoryJDBCImplTest {
    @Mock
    JdbcTemplate jdbc;

    @InjectMocks
    EmployeeRepositoryJDBCImpl repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllEmployeesWhenFindAllIsCalled() {
        Employee expectedEmployee = new Employee(101, "Yousuf","Shaik","yousufbabashaik@gmail.com","Dev", new BigDecimal("123456"));
        when(jdbc.query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<Employee>>any()))
                .thenReturn(List.of(expectedEmployee));
        List<Employee> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals(expectedEmployee, all.get(0));
    }
}
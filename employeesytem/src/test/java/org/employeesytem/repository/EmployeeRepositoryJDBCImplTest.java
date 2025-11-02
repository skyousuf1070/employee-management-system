package org.employeesytem.repository;

import org.employeesytem.dto.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
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
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));
        when(jdbc.query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<Employee>>any()))
                .thenReturn(List.of(expectedEmployee));
        List<Employee> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals(expectedEmployee, all.get(0));
    }

    @Test
    void shouldSaveTheEmployeeSuccessfullyWhenTheIdIsUnique() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));

        Employee actualEmployee = repository.save(expectedEmployee);

        assertEquals(expectedEmployee, actualEmployee);
        verify(jdbc).update(anyString(),
                eq(expectedEmployee.getId()),
                eq(expectedEmployee.getFirstName()),
                eq(expectedEmployee.getLastName()),
                eq(expectedEmployee.getEmail()),
                eq(expectedEmployee.getDepartment()),
                eq(expectedEmployee.getSalary()));
    }

    @Test
    void shouldThrowTheExceptionWhenTheIdExists() {
        Employee duplicateEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));

        when(jdbc.update(anyString(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new DuplicateKeyException("Duplicate entry for id"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repository.save(duplicateEmployee));
        assertTrue(exception.getMessage().contains("already exists"));
    }
}
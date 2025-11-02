package org.employeesytem.service;

import org.employeesytem.dto.Employee;
import org.employeesytem.repository.EmployeeRepositoryJDBCImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {
    @Mock
    private EmployeeRepositoryJDBCImpl repository;

    @InjectMocks
    private EmployeeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllEmployeesWhenFindAllEmployeesIsCalled() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));

        when(repository.findAll()).thenReturn(List.of(expectedEmployee));

        List<Employee> allEmployees = service.findAllEmployees();
        assertEquals(1, allEmployees.size());
        assertEquals(expectedEmployee, allEmployees.get(0));
    }
}
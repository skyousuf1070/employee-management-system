package org.employeesytem.service;

import org.employeesytem.dto.Employee;
import org.employeesytem.exceptions.DuplicateEmployeeException;
import org.employeesytem.exceptions.EmployeeNotFoundException;
import org.employeesytem.repository.EmployeeRepositoryJDBCImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    public void shouldAddAnEmployeeWhenTheIdIsUnique() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));

        when(repository.save(any(Employee.class))).thenReturn(expectedEmployee);

        Employee actualEmployee = service.addEmployee(expectedEmployee);
        assertEquals(expectedEmployee, actualEmployee);
    }

    @Test
    void shouldThrowDuplicateEmployeeExceptionWhenIdAlreadyExists() {
        Employee employee = new Employee(101, "Yousuf", "Shaik", "yousuf@gmail.com", "Dev", new BigDecimal("50000"));

        when(repository.save(employee))
                .thenThrow(new IllegalArgumentException("Employee with id 101 already exists"));

        DuplicateEmployeeException ex = assertThrows(
                DuplicateEmployeeException.class,
                () -> service.addEmployee(employee)
        );

        assertEquals("Employee with id 101 already exists", ex.getMessage());
    }

    @Test
    void shouldReturnTheEmployeesWhenIdExists() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));

        when(repository.findById(101)).thenReturn(Optional.of(expectedEmployee));

        Employee actualEmployee = service.findByEmployeeId(101);
        assertEquals(expectedEmployee, actualEmployee);
    }

    @Test
    void shouldThrowEmployeeNotFoundExceptionWhenIdNotExists() {
        when(repository.findById(101))
                .thenThrow(new EmployeeNotFoundException("Employee with id 101 not exists"));

        EmployeeNotFoundException ex = assertThrows(
                EmployeeNotFoundException.class,
                () -> service.findByEmployeeId(101)
        );

        assertEquals("Employee with id 101 not exists", ex.getMessage());
    }
}
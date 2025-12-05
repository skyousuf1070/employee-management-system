package org.employeesytem.service;

import org.employeesytem.dto.Employee;
import org.employeesytem.exceptions.DuplicateEmployeeException;
import org.employeesytem.exceptions.EmployeeNotFoundException;
import org.employeesytem.repository.jpa.EmployeeRepositoryJPAImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class EmployeeServiceTest {
    @Mock
    private EmployeeRepositoryJPAImpl repository;

    @InjectMocks
    private EmployeeService service;

    private Employee expectedEmployee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));
    }

    @Test
    void shouldReturnAllEmployeesWhenFindAllEmployeesIsCalled() {
        when(repository.findAll(PageRequest.of(0, 5))).thenReturn(new PageImpl<>(List.of(expectedEmployee)));

        Page<Employee> allEmployees = service.findAllEmployees(0, 5);
        assertEquals(1, allEmployees.getTotalElements());
        assertEquals(expectedEmployee, allEmployees.getContent().get(0));
    }

    @Test
    public void shouldAddAnEmployeeWhenTheIdIsUnique() {
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

    @Test
    void shouldUpdateEmployeeWhenIdExists() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousuf.new@gmail.com", "Dev", new BigDecimal("123456"));

        when(repository.save(expectedEmployee)).thenReturn(expectedEmployee);

        Employee actualEmployee = service.updateEmployee(101, expectedEmployee);
        assertEquals(expectedEmployee, actualEmployee);
    }

    @Test
    void shouldThrowEmployeeNotFoundExceptionWhenIdNotExistsForUpdate() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousuf.new@gmail.com", "Dev", new BigDecimal("123456"));

        when(repository.save(expectedEmployee))
                .thenThrow(new EmployeeNotFoundException("Employee with ID 101 not found"));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> service.updateEmployee(101, expectedEmployee));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void shouldDeleteEmployeeWhenIdExists() {
        doNothing().when(repository).deleteById(101);

        service.deleteEmployee(101);

        verify(repository).deleteById(101);
    }

    @Test
    void shouldThrowEmployeeNotFoundExceptionWhenIdNotExistsForDelete() {
        doThrow(new EmployeeNotFoundException("Employee with ID 101 not found"))
                .when(repository).deleteById(101);

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> service.deleteEmployee(101));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void shouldReturnZeroWhenGetEmployeeCountIsCalled() {
        when(repository.count()).thenReturn(0L);

        Long actualCount = service.getEmployeeCount();
        assertEquals(0, actualCount);
    }

    @Test
    void shouldReturnAllEmployeesCountWhenGetEmployeeCountIsCalled() {
        when(repository.count()).thenReturn(10L);

        Long actualCount = service.getEmployeeCount();
        assertEquals(10, actualCount);
    }
}
package org.employeesytem.repository.jpa;

import org.employeesytem.dto.Employee;
import org.employeesytem.exceptions.EmployeeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class EmployeeRepositoryJPAImplTest {

    @InjectMocks
    EmployeeRepositoryJPAImpl repository;

    @Mock
    EmployeeJPARepository jpa;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllEmployeesWhenFindAllIsCalled() {
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("firstName").ascending());
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));
        when(jpa.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of(expectedEmployee)));

        Page<Employee> all = repository.findAll(pageRequest);

        assertEquals(1, all.getTotalElements());
        assertEquals(expectedEmployee, all.getContent().get(0));
        verify(jpa).findAll(pageRequest);
    }

    @Test
    void shouldSaveTheEmployeeSuccessfullyWhenTheIdIsUnique() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));
        when(jpa.save(expectedEmployee)).thenReturn(expectedEmployee);

        Employee actualEmployee = repository.save(expectedEmployee);

        assertEquals(expectedEmployee, actualEmployee);
        verify(jpa).save(expectedEmployee);
    }

    @Test
    void shouldThrowTheExceptionWhenTheIdExists() {
        Employee duplicateEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));
        when(jpa.save(duplicateEmployee)).thenThrow(new DuplicateKeyException("Duplicate entry for id"));

        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class,
                () -> repository.save(duplicateEmployee));

        assertTrue(exception.getMessage().contains("Duplicate entry for id"));
    }

    @Test
    void shouldReturnEmployeeWhenIdExists() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "Dev", new BigDecimal("123456"));
        when(jpa.findById(101)).thenReturn(Optional.of(expectedEmployee));
        Optional<Employee> actualEmployee = repository.findById(101);
        actualEmployee.ifPresent(employee -> assertEquals(expectedEmployee, employee));
    }

    @Test
    void shouldReturnEmptyWhenIdNotExists() {
        when(jpa.findById(101)).thenReturn(Optional.empty());
        Optional<Employee> actualEmployee = repository.findById(101);
        assertEquals(Optional.empty(), actualEmployee);
    }

    @Test
    void shouldUpdateEmployeeWhenIdExists() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousuf.new@gmail.com", "Dev", new BigDecimal("123456"));

        when(jpa.save(expectedEmployee)).thenReturn(expectedEmployee);

        Employee actualEmployee = repository.save(expectedEmployee);
        assertEquals(expectedEmployee, actualEmployee);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenTheUpdateFails() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousuf.new@gmail.com", "Dev", new BigDecimal("123456"));

        when(jpa.save(expectedEmployee)).thenThrow(new RuntimeException("Failed to update employee with ID 101"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> repository.save(expectedEmployee));
        assertTrue(exception.getMessage().contains("Failed to update employee with ID"));
    }

    @Test
    void shouldThrowEmployeeNotFoundExceptionWhenIdNotExists() {
        Employee expectedEmployee = new Employee(101, "Yousuf", "Shaik", "yousuf.new@gmail.com", "Dev", new BigDecimal("123456"));

        when(jpa.save(expectedEmployee)).thenThrow(new EmployeeNotFoundException("Employee with " + 101 + " not found"));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> repository.save(expectedEmployee));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void shouldDeleteEmployeeWhenIdExists() {
        doNothing().when(jpa).deleteById(101);

        repository.deleteById(101);

        verify(jpa).deleteById(101);
    }

    @Test
    void shouldThrowEmployeeNotFoundExceptionWhenIdNotExistsToDelete() {
        doThrow(new EmployeeNotFoundException("Employee with " + 101 + " not found")).when(jpa).deleteById(101);

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> repository.deleteById(101));

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void shouldReturnZeroWhenCountIsCalled() {
        when(jpa.count()).thenReturn(0L);

        Long count = repository.count();

        assertEquals(0, count);
        verify(jpa).count();
    }

    @Test
    void shouldReturnAllEmployeesCountWhenCountIsCalled() {
        when(jpa.count()).thenReturn(10L);

        Long count = repository.count();

        assertEquals(10, count);
        verify(jpa).count();
    }
}
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class EmployeeRepositoryJPAImplTest {

    @InjectMocks
    EmployeeRepositoryJPAImpl repository;

    @Mock
    EmployeeJPARepository jpa;

    private Employee employee;
    private PageRequest pageRequest;
    private Sort sort;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee(101, "Yousuf", "Shaik", "yousufbabashaik@gmail.com", "IT", new BigDecimal("123456"));
        sort = Sort.by("firstName").ascending();
        pageRequest = PageRequest.of(0, 5, sort);
    }

    @Test
    void shouldReturnAllEmployeesWhenFindAllIsCalled() {
        when(jpa.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of(employee)));

        Page<Employee> all = repository.findAll(pageRequest);

        assertEquals(1, all.getTotalElements());
        assertEquals(employee, all.getContent().get(0));
        verify(jpa).findAll(pageRequest);
    }

    @Test
    void shouldSaveTheEmployeeSuccessfullyWhenTheIdIsUnique() {
        when(jpa.save(employee)).thenReturn(employee);

        Employee actualEmployee = repository.save(employee);

        assertEquals(employee, actualEmployee);
        verify(jpa).save(employee);
    }

    @Test
    void shouldThrowTheExceptionWhenTheIdExists() {
        when(jpa.save(employee)).thenThrow(new DuplicateKeyException("Duplicate entry for id"));

        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class,
                () -> repository.save(employee));

        assertTrue(exception.getMessage().contains("Duplicate entry for id"));
    }

    @Test
    void shouldReturnEmployeeWhenIdExists() {
        when(jpa.findById(101)).thenReturn(Optional.of(employee));
        Optional<Employee> actualEmployee = repository.findById(101);
        actualEmployee.ifPresent(employee1 -> assertEquals(employee1, employee));
    }

    @Test
    void shouldReturnEmptyWhenIdNotExists() {
        when(jpa.findById(101)).thenReturn(Optional.empty());
        Optional<Employee> actualEmployee = repository.findById(101);
        assertEquals(Optional.empty(), actualEmployee);
    }

    @Test
    void shouldUpdateEmployeeWhenIdExists() {
        when(jpa.save(employee)).thenReturn(employee);

        Employee actualEmployee = repository.save(employee);
        assertEquals(employee, actualEmployee);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenTheUpdateFails() {
        when(jpa.save(employee)).thenThrow(new RuntimeException("Failed to update employee with ID 101"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> repository.save(employee));
        assertTrue(exception.getMessage().contains("Failed to update employee with ID"));
    }

    @Test
    void shouldThrowEmployeeNotFoundExceptionWhenIdNotExists() {
        when(jpa.save(employee)).thenThrow(new EmployeeNotFoundException("Employee with " + 101 + " not found"));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> repository.save(employee));
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

    @Test
    void shouldReturnAllNameMatchedEmployeesWhenNameIsNotNull() {
        String name = "Yousuf";
        when(jpa.findByCriteria(name, null, pageRequest)).thenReturn(new PageImpl<>(List.of(employee)));

        Page<Employee> all = repository.findByCriteria(name, null, pageRequest);

        assertEquals(1, all.getTotalElements());
        assertEquals(employee, all.getContent().get(0));
        verify(jpa, times(1)).findByCriteria(name, null, pageRequest);
    }

    @Test
    void shouldReturnAllDepartmentMatchedEmployeesWhenDepartmentIsNotNull() {
        String department = "IT";
        when(jpa.findByCriteria(null, department, pageRequest)).thenReturn(new PageImpl<>(List.of(employee)));

        Page<Employee> all = repository.findByCriteria(null, department, pageRequest);

        assertEquals(1, all.getTotalElements());
        assertEquals(employee, all.getContent().get(0));
        verify(jpa, times(1)).findByCriteria(null, department, pageRequest);
    }

    @Test
    void shouldReturnAllListOfEmployeesWhenFindAllIsCalledWithoutPaginationRequest() {
        when(jpa.findAll(sort)).thenReturn(List.of(employee));

        List<Employee> all = repository.findAll(sort);

        assertEquals(1, all.size());
        assertEquals(employee, all.get(0));
        verify(jpa).findAll(sort);
    }

    @Test
    void shouldReturnAllDepartmentMatchedEmployeesWhenDepartmentIsPassedForExport() {
        String department = "IT";
        when(jpa.findByCriteriaForExport(null, department, sort)).thenReturn((List.of(employee)));

        List<Employee> all = repository.findByCriteriaForExport(null, department, sort);

        assertEquals(1, all.size());
        assertEquals(employee, all.get(0));
        verify(jpa, times(1)).findByCriteriaForExport(null, department, sort);
    }

    @Test
    void shouldReturnAllNameMatchedEmployeesWhenNameIsPassedForExport() {
        String name = "Yousuf";
        when(jpa.findByCriteriaForExport(name, null, sort)).thenReturn((List.of(employee)));

        List<Employee> all = repository.findByCriteriaForExport(name, null, sort);

        assertEquals(1, all.size());
        assertEquals(employee, all.get(0));
        verify(jpa, times(1)).findByCriteriaForExport(name, null, sort);
    }

    @Test
    void shouldReturnAllNameDepartmentMatchedEmployeesWhenNameAndDepartmentArePassedForExport() {
        String name = "Yousuf", department = "IT";
        when(jpa.findByCriteriaForExport(name, department, sort)).thenReturn((List.of(employee)));

        List<Employee> all = repository.findByCriteriaForExport(name, department, sort);

        assertEquals(1, all.size());
        assertEquals(employee, all.get(0));
        verify(jpa, times(1)).findByCriteriaForExport(name, department, sort);
    }
}
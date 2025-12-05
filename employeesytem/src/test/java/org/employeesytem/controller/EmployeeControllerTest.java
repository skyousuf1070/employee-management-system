package org.employeesytem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.employeesytem.dto.Employee;
import org.employeesytem.exceptions.DuplicateEmployeeException;
import org.employeesytem.exceptions.EmployeeNotFoundException;
import org.employeesytem.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private EmployeeService employeeService;

    @Test
    public void shouldReturnZeroEmployees() throws Exception {
        when(employeeService.findAllEmployees(0, 5)).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    public void shouldReturnAllEmployees() throws Exception {
        Employee employee1 = new Employee(1, "Alice", "A", "abc@gmail.com", "Dev", BigDecimal.valueOf(50000));
        Employee employee2 = new Employee(2, "Bob", "B", "bca@gmail.com", "HR", BigDecimal.valueOf(100000));
        List<Employee> mockEmployees = List.of(employee1, employee2);

        when(employeeService.findAllEmployees(0, 5)).thenReturn(new PageImpl<>((mockEmployees)));

        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.content[0].lastName").value("A"))
                .andExpect(jsonPath("$.content[0].email").value("abc@gmail.com"))
                .andExpect(jsonPath("$.content[0].department").value("Dev"))
                .andExpect(jsonPath("$.content[0].salary").value("50000"))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    public void shouldAddAnEmployeeWhenTheIdIsUnique() throws Exception {
        Employee savedEmployee = new Employee(101, "Bob", "B", "bca@gmail.com", "HR", BigDecimal.valueOf(100000));
        String expectedJson = objectMapper.writeValueAsString(savedEmployee);

        when(employeeService.addEmployee(any(Employee.class))).thenReturn(savedEmployee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldReturnConflictWhenEmployeeAlreadyExists() throws Exception {
        Employee existingEmployee = new Employee(101, "Yousuf", "Shaik",
                "yousuf@gmail.com", "Dev", new BigDecimal("123456"));

        String jsonRequest = objectMapper.writeValueAsString(existingEmployee);

        when(employeeService.addEmployee(existingEmployee))
                .thenThrow(new DuplicateEmployeeException("Employee with id 101 already exists"));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(content().string("Employee with id 101 already exists"));
    }

    @Test
    public void shouldReturnEmployeeWhenIdExists() throws Exception {
        Employee expectedEmployee = new Employee(1, "Alice", "A", "abc@gmail.com", "Dev", BigDecimal.valueOf(50000));
        String expectedResult = objectMapper.writeValueAsString(expectedEmployee);

        when(employeeService.findByEmployeeId(1)).thenReturn(expectedEmployee);

        mockMvc.perform(get("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResult));
    }

    @Test
    void shouldReturnNotFoundWhenIdNotExists() throws Exception {
        when(employeeService.findByEmployeeId(101))
                .thenThrow(new EmployeeNotFoundException("Employee with id 101 not exists"));

        mockMvc.perform(get("/api/v1/employees/101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee with id 101 not exists"));
    }

    @Test
    public void shouldUpdateEmployeeWhenIdExists() throws Exception {
        Employee expectedEmployee = new Employee(1, "Alice", "A", "abc.new@gmail.com", "Dev", BigDecimal.valueOf(50000));
        String expectedResult = objectMapper.writeValueAsString(expectedEmployee);

        when(employeeService.updateEmployee(1, expectedEmployee)).thenReturn(expectedEmployee);

        mockMvc.perform(put("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedResult))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResult));
    }

    @Test
    public void shouldThrowEmployeeNotFoundWhenIdNotExistsForUpdate() throws Exception {
        Employee expectedEmployee = new Employee(1, "Alice", "A", "abc.new@gmail.com", "Dev", BigDecimal.valueOf(50000));
        String expectedResult = objectMapper.writeValueAsString(expectedEmployee);

        when(employeeService.updateEmployee(1, expectedEmployee))
                .thenThrow(new EmployeeNotFoundException("Employee with ID 1 not found"));

        mockMvc.perform(put("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedResult))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee with ID 1 not found"));
    }

    @Test
    public void shouldDeleteEmployeeWhenIdExists() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1);

        mockMvc.perform(delete("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldThrowEmployeeNotFoundWhenIdNotExistsForDelete() throws Exception {
        doThrow(new EmployeeNotFoundException("Employee with ID 101 not found"))
                .when(employeeService).deleteEmployee(1);

        mockMvc.perform(delete("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee with ID 101 not found"));
    }

    @Test
    public void shouldReturnZeroCount() throws Exception {
        when(employeeService.getEmployeeCount()).thenReturn(0L);

        mockMvc.perform(get("/api/v1/employees/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(String.valueOf(0)));
    }

    @Test
    public void shouldReturnAllEmployeesCount() throws Exception {
        when(employeeService.getEmployeeCount()).thenReturn(10L);

        mockMvc.perform(get("/api/v1/employees/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(String.valueOf(10)));
    }
}

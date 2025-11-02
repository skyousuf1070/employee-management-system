package org.employeesytem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.employeesytem.dto.Employee;
import org.employeesytem.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private EmployeeService employeeService;

    @Test
    public void shouldReturnZeroEmployees() throws Exception {
        when(employeeService.findAllEmployees()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnAllEmployees() throws Exception {
        Employee employee1 = new Employee(1, "Alice", "A", "abc@gmail.com", "Dev", BigDecimal.valueOf(50000));
        Employee employee2 = new Employee(2, "Bob", "B", "bca@gmail.com", "HR", BigDecimal.valueOf(100000));
        List<Employee> mockEmployees = List.of(employee1, employee2);
        String expectedResult = objectMapper.writeValueAsString(mockEmployees);

        when(employeeService.findAllEmployees()).thenReturn(mockEmployees);

        mockMvc.perform(get("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResult));
    }
}

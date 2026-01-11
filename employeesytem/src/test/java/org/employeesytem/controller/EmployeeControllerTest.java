package org.employeesytem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.employeesytem.dto.Employee;
import org.employeesytem.exceptions.DuplicateEmployeeException;
import org.employeesytem.exceptions.EmployeeNotFoundException;
import org.employeesytem.service.EmployeeService;
import org.employeesytem.util.CSVExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
    @MockBean
    private CSVExporter exporter;

    private Employee employee;
    private Sort sort;

    @BeforeEach
    public void setUp() {
        employee = new Employee(101, "Yousuf", "Shaik", "yousuf@gmail.com", "IT", new BigDecimal("123456"));
        sort = Sort.by("firstName").ascending();
    }

    @Test
    public void shouldReturnZeroEmployees() throws Exception {
        when(employeeService.findAllEmployees(any(Pageable.class), isNull(), isNull())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "firstName,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(0));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeService, times(1)).findAllEmployees(pageableCaptor.capture(), isNull(), isNull());
        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(5);
        Sort capturedSort = capturedPageable.getSort();
        Sort.Order order = capturedSort.getOrderFor("firstName");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    public void shouldReturnAllEmployees() throws Exception {
        Employee employee2 = new Employee(102, "Bob", "B", "bca@gmail.com", "HR", BigDecimal.valueOf(100000));
        List<Employee> mockEmployees = List.of(employee, employee2);

        when(employeeService.findAllEmployees(any(Pageable.class), isNull(), isNull())).thenReturn(new PageImpl<>((mockEmployees)));

        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "firstName,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(101))
                .andExpect(jsonPath("$.content[0].firstName").value("Yousuf"))
                .andExpect(jsonPath("$.content[0].lastName").value("Shaik"))
                .andExpect(jsonPath("$.content[0].email").value("yousuf@gmail.com"))
                .andExpect(jsonPath("$.content[0].department").value("IT"))
                .andExpect(jsonPath("$.content[0].salary").value("123456"))
                .andExpect(jsonPath("$.page.totalElements").value(2));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeService, times(1)).findAllEmployees(pageableCaptor.capture(), isNull(), isNull());
        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(5);
        Sort capturedSort = capturedPageable.getSort();
        Sort.Order order = capturedSort.getOrderFor("firstName");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    public void shouldAddAnEmployeeWhenTheIdIsUnique() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(employee);

        when(employeeService.addEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldReturnConflictWhenEmployeeAlreadyExists() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(employee);

        when(employeeService.addEmployee(employee)).thenThrow(new DuplicateEmployeeException("Employee with id 101 already exists"));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(content().string("Employee with id 101 already exists"));
    }

    @Test
    public void shouldReturnEmployeeWhenIdExists() throws Exception {
        String expectedResult = objectMapper.writeValueAsString(employee);

        when(employeeService.findByEmployeeId(1)).thenReturn(employee);

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
        Employee expectedEmployee = new Employee(1, "Alice", "A", "abc.new@gmail.com", "IT", BigDecimal.valueOf(50000));
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
        Employee expectedEmployee = new Employee(1, "Alice", "A", "abc.new@gmail.com", "IT", BigDecimal.valueOf(50000));
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

    @Test
    void shouldReturnBadRequestWhenEmployeeValidationFails() throws Exception {
        Employee invalidEmployee = new Employee(1, "", "", "bademail", "", null);
        String jsonRequest = objectMapper.writeValueAsString(invalidEmployee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.department").exists())
                .andExpect(jsonPath("$.salary").exists());
    }

    @Test
    void shouldReturnBadRequestWhenSalaryIsLessThanOrEqualZero() throws Exception {
        Employee invalidEmployee = new Employee(1, "Yousuf", "Baba", "yousuf@gmail.com", "IT", BigDecimal.ZERO);
        String jsonRequest = objectMapper.writeValueAsString(invalidEmployee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.salary").exists());
    }

    @Test
    void shouldReturnBadRequestWhenBadEmailIsPassed() throws Exception {
        Employee invalidEmployee = new Employee(1, "Yousuf", "Baba", "bademail", "IT", BigDecimal.valueOf(100000));
        String jsonRequest = objectMapper.writeValueAsString(invalidEmployee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void shouldReturnBadRequestForExcessiveNameLength() throws Exception {
        String excessivelyLongName = "A".repeat(51);
        Employee invalidEmployee = new Employee(1, excessivelyLongName, excessivelyLongName, "valid@example.com", "HR", BigDecimal.valueOf(50000));
        String jsonRequest = objectMapper.writeValueAsString(invalidEmployee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists());
    }

    @Test
    void shouldReturnBadRequestForInvalidDepartmentValue() throws Exception {
        Employee invalidEmployee = new Employee(1, "Valid", "Name", "valid@example.com", "CLEANING", BigDecimal.valueOf(50000));
        String jsonRequest = objectMapper.writeValueAsString(invalidEmployee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.department").exists())
                .andExpect(jsonPath("$.department").value("Department must be one of: HR, IT, MARKETING, SALES, FINANCE"));
    }

    @Test
    void shouldReturnBadRequestWhenPageIsNegative() throws Exception {
        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "-1")
                        .param("size", "5")
                        .param("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Page number cannot be negative.")));
    }

    @Test
    void shouldReturnBadRequestWhenSizeIsZero() throws Exception {
        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "0")
                        .param("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Page size must be at least 1.")));
    }

    @Test
    void shouldReturnBadRequestWhenDeleteIdIsZero() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Employee ID must be positive.")));
    }

    @Test
    void shouldReturnBadRequestWhenGetIdIsZero() throws Exception {
        mockMvc.perform(get("/api/v1/employees/0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Employee ID must be positive.")));
    }

    @Test
    void shouldReturnBadRequestWhenUpdateIdIsZero() throws Exception {
        String expectedResult = objectMapper.writeValueAsString(employee);

        mockMvc.perform(put("/api/v1/employees/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedResult))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Employee ID must be positive.")));
    }

    @Test
    public void shouldReturnAllNameMatchedEmployeesWhenNameIsPassed() throws Exception {
        Employee employee2 = new Employee(102, "Shaik Yousuf Baba", "B", "bca@gmail.com", "HR", BigDecimal.valueOf(100000));
        List<Employee> mockEmployees = List.of(employee, employee2);

        when(employeeService.findAllEmployees(any(Pageable.class), eq("Yousuf"), isNull())).thenReturn(new PageImpl<>((mockEmployees)));

        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "5")
                        .param("name", "Yousuf")
                        .param("sort", "firstName,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(101))
                .andExpect(jsonPath("$.content[0].firstName").value("Yousuf"))
                .andExpect(jsonPath("$.content[0].lastName").value("Shaik"))
                .andExpect(jsonPath("$.content[0].email").value("yousuf@gmail.com"))
                .andExpect(jsonPath("$.content[0].department").value("IT"))
                .andExpect(jsonPath("$.content[0].salary").value("123456"))
                .andExpect(jsonPath("$.page.totalElements").value(2));
        verify(employeeService, times(1)).findAllEmployees(any(Pageable.class), eq("Yousuf"), isNull());
    }

    @Test
    public void shouldReturnAllDepartmentMatchedEmployeesWhenDepartmentIsPassed() throws Exception {
        Employee employee2 = new Employee(102, "Laddu", "B", "bca@gmail.com", "IT", BigDecimal.valueOf(100000));
        List<Employee> mockEmployees = List.of(employee, employee2);

        when(employeeService.findAllEmployees(any(Pageable.class), isNull(), eq("IT"))).thenReturn(new PageImpl<>((mockEmployees)));

        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "5")
                        .param("department", "IT")
                        .param("sort", "firstName,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(101))
                .andExpect(jsonPath("$.content[0].firstName").value("Yousuf"))
                .andExpect(jsonPath("$.content[0].lastName").value("Shaik"))
                .andExpect(jsonPath("$.content[0].email").value("yousuf@gmail.com"))
                .andExpect(jsonPath("$.content[0].department").value("IT"))
                .andExpect(jsonPath("$.content[0].salary").value("123456"))
                .andExpect(jsonPath("$.page.totalElements").value(2));
        verify(employeeService, times(1)).findAllEmployees(any(Pageable.class), isNull(), eq("IT"));
    }

    @Test
    void shouldReturnBadRequestForInvalidDepartmentParam() throws Exception {
        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "5")
                        .param("department", "HOUSEKEEPING")
                        .param("sort", "firstName,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Department must be one of: HR, IT, MARKETING, SALES, FINANCE"));
    }

    @Test
    void shouldExportAllEmployeesToCSVSortByParameterIsPassed() throws Exception {
        Employee employee2 = new Employee(102, "Laddu", "B", "bca@gmail.com", "HR", BigDecimal.valueOf(100000.50));
        List<Employee> mockEmployees = List.of(employee, employee2);
        String csvContent = "ID,First Name,Last Name,Email,Department,Salary\n" +
                "101,\"Yousuf\",\"Shaik\",\"yousuf@gmail.com\",\"IT\",123456.00\n" +
                "102,\"Laddu\",\"B\",\"bca@gmail.com\",\"HR\",100000.50\n";
        byte[] mockCsvBytes = csvContent.getBytes(StandardCharsets.UTF_8);
        when(employeeService.findAllEmployeesForExport(null, null, sort)).thenReturn(mockEmployees);
        when(exporter.writeEmployeesToCsv(anyList())).thenReturn(mockCsvBytes);

        mockMvc.perform(get("/api/v1/employees/export")
                        .param("sort", "firstName,asc")
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                // Assert Content Disposition header for file download
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        allOf(
                                startsWith("attachment; filename=\"employees_"),
                                endsWith(".csv\""))))
                .andExpect(content().string(not(emptyOrNullString())))
                .andExpect(content().string(containsString("ID,First Name,Last Name,Email,Department,Salary")))
                .andExpect(content().string(containsString("101,\"Yousuf\",\"Shaik\",\"yousuf@gmail.com\",\"IT\",123456.00")))
                .andExpect(content().string(containsString("102,\"Laddu\",\"B\",\"bca@gmail.com\",\"HR\",100000.50")));
        verify(employeeService, times(1)).findAllEmployeesForExport(null, null, sort);
    }

    @Test
    void shouldExportAllEmployeesToCSVSortByParameterAndNameAndDepartmentArePassed() throws Exception {
        String department = "IT";
        String name = "Yousuf";
        Employee employee2 = new Employee(102, "Laddu", name, "bca@gmail.com", department, BigDecimal.valueOf(100000.50));
        List<Employee> mockEmployees = List.of(employee, employee2);
        String csvContent = "ID,First Name,Last Name,Email,Department,Salary\n" +
                "101,\"Yousuf\",\"Shaik\",\"yousuf@gmail.com\",\"IT\",123456.00\n" +
                "102,\"Laddu\",\"Yousuf\",\"bca@gmail.com\",\"IT\",100000.50\n";
        byte[] mockCsvBytes = csvContent.getBytes(StandardCharsets.UTF_8);
        when(employeeService.findAllEmployeesForExport(name, department, sort)).thenReturn(mockEmployees);
        when(exporter.writeEmployeesToCsv(anyList())).thenReturn(mockCsvBytes);

        mockMvc.perform(get("/api/v1/employees/export")
                        .param("name", name)
                        .param("department", department)
                        .param("sort", "firstName,asc")
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                // Assert Content Disposition header for file download
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        allOf(
                                startsWith("attachment; filename=\"employees_"),
                                endsWith(".csv\""))))
                .andExpect(content().string(not(emptyOrNullString())))
                .andExpect(content().string(containsString("ID,First Name,Last Name,Email,Department,Salary")))
                .andExpect(content().string(containsString("101,\"Yousuf\",\"Shaik\",\"yousuf@gmail.com\",\"IT\",123456.00")))
                .andExpect(content().string(containsString("102,\"Laddu\",\"Yousuf\",\"bca@gmail.com\",\"IT\",100000.50")));
        verify(employeeService, times(1)).findAllEmployeesForExport(name, department, sort);
    }
}

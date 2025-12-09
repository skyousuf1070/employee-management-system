package org.employeesytem.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.employeesytem.dto.Employee;
import org.employeesytem.service.EmployeeService;
import org.employeesytem.validation.DepartmentValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("api/v1/employees")
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<Page<Employee>> findAllEmployees(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative.") int page,
            @RequestParam(defaultValue = "5")
            @Min(value = 1, message = "Page size must be at least 1.") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DepartmentValue String department,
            Sort sort) {
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Employee> allEmployees = employeeService.findAllEmployees(pageRequest, name, department);
        return ResponseEntity.ok(allEmployees);
    }

    @PostMapping
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody Employee employee) {
        Employee savedEmployee = employeeService.addEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findEmployeeById(
            @Min(value = 1, message = "Employee ID must be positive.")
            @PathVariable int id) {
        return ResponseEntity.ok(employeeService.findByEmployeeId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @Min(value = 1, message = "Employee ID must be positive.")
            @PathVariable int id,
            @Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @Min(value = 1, message = "Employee ID must be positive.")
            @PathVariable int id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getEmployeeCount() {
        return ResponseEntity.ok(employeeService.getEmployeeCount());
    }
}

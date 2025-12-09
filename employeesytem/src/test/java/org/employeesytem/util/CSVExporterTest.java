package org.employeesytem.util;

import org.employeesytem.dto.Employee;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CSVExporterTest {
    private final CSVExporter csvExporter = new CSVExporter();

    @Test
    void shouldWriteCorrectHeaderAndData() {
        Employee employee1 = new Employee(101, "Yousuf", "Shaik", "yousuf@gmail.com", "IT", BigDecimal.valueOf(123456.789));
        Employee employee2 = new Employee(102, "Laddu", "B", "bca@gmail.com", "HR", BigDecimal.valueOf(100000));
        List<Employee> employees = List.of(employee1, employee2);
        String newLine = System.lineSeparator();
        String expectedCsv =
                "ID,First Name,Last Name,Email,Department,Salary" + newLine +
                        "101,\"Yousuf\",\"Shaik\",\"yousuf@gmail.com\",\"IT\",123456.79" + newLine +
                        "102,\"Laddu\",\"B\",\"bca@gmail.com\",\"HR\",100000.00" + newLine;

        byte[] resultBytes = csvExporter.writeEmployeesToCsv(employees);
        String resultString = new String(resultBytes, StandardCharsets.UTF_8);

        assertEquals(expectedCsv, resultString, "The generated CSV content does not match the expected format.");
    }

    @Test
    void shouldReturnOnlyHeaderForEmptyList() {
        List<Employee> employees = Collections.emptyList();
        String newLine = System.lineSeparator();
        String expectedCsv = "ID,First Name,Last Name,Email,Department,Salary" + newLine;

        byte[] resultBytes = csvExporter.writeEmployeesToCsv(employees);
        String resultString = new String(resultBytes, StandardCharsets.UTF_8);

        assertEquals(expectedCsv, resultString, "The CSV should only contain the header for an empty list.");
    }
}
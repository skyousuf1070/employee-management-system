package org.employeesytem.util;

import org.employeesytem.dto.Employee;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.math.RoundingMode;

@Component
public class CSVExporter {

    public byte[] writeEmployeesToCsv(List<Employee> employees) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8)) {

            // Write the CSV Header
            writer.println("ID,First Name,Last Name,Email,Department,Salary");

            // Write the Employee Data Rows
            for (Employee employee : employees) {
                String salaryFormatted = employee.getSalary()
                        .setScale(2, RoundingMode.HALF_UP)
                        .toString();

                String line = String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",%s",
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail(),
                        employee.getDepartment(),
                        salaryFormatted
                );
                writer.println(line);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV content.", e);
        }
    }
}
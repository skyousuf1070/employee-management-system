package org.employeesytem.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.employeesytem.dto.Department;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepartmentValueValidator implements ConstraintValidator<DepartmentValue, String> {
    private String validValues;

    @Override
    public void initialize(DepartmentValue constraintAnnotation) {
        validValues = Stream.of(Department.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        boolean isValidValue = Stream.of(Department.values())
                .anyMatch(department -> value.toUpperCase().equals(department.name()));
        if (isValidValue) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Department must be one of: " + validValues)
                    .addConstraintViolation();
            return false;
        }
    }
}

package org.employeesytem.repository.jpa;

import org.employeesytem.dto.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeJPARepository extends JpaRepository<Employee, Integer> {
}

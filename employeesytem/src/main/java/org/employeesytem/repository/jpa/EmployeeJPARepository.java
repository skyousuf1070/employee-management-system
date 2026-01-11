package org.employeesytem.repository.jpa;

import org.employeesytem.dto.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeJPARepository extends JpaRepository<Employee, Integer> {
    @Query("SELECT e FROM Employee e WHERE " +
            "(:name IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :name, '%')))" +
            "AND (:department IS NULL OR LOWER(e.department) = LOWER(:department))")
    Page<Employee> findByCriteria(@Param("name") String name,
                                  @Param("department") String department, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE " +
            "(:name IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :name, '%')))" +
            "AND (:department IS NULL OR LOWER(e.department) = LOWER(:department))")
    List<Employee> findByCriteriaForExport(@Param("name") String name,
                                           @Param("department") String department, Sort sort);
}

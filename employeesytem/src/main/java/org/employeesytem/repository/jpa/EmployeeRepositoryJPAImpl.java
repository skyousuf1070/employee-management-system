package org.employeesytem.repository.jpa;

import org.employeesytem.dto.Employee;
import org.employeesytem.repository.EmployeeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("jpa")
public class EmployeeRepositoryJPAImpl implements EmployeeRepository {
    private final EmployeeJPARepository jpa;

    public EmployeeRepositoryJPAImpl(EmployeeJPARepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Page<Employee> findAll(Pageable pageable) {
        return jpa.findAll(pageable);
    }

    @Override
    public Employee save(Employee employee) {
        return jpa.save(employee);
    }

    @Override
    public Optional<Employee> findById(int id) {
        return jpa.findById(id);
    }

    @Override
    public void deleteById(int id) {
        jpa.deleteById(id);
    }

    @Override
    public Long count() {
        return jpa.count();
    }

    @Override
    public Page<Employee> findByCriteria(String name, Pageable pageable) {
        return jpa.findByCriteria(name, pageable);
    }
}

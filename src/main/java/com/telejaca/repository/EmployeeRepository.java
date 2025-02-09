package com.telejaca.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telejaca.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByEmail(String email);
    
    Employee findByUsername(String username);
    
    boolean existsByUsername(String username);
}

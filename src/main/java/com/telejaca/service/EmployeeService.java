package com.telejaca.service;

import com.telejaca.model.Employee;
import com.telejaca.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeByUsername(String username) {
        return employeeRepository.findById(username);
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public void updateEmployee(Employee employee) {
        if (employeeRepository.existsById(employee.getUsername())) {
            employeeRepository.save(employee);
        }
    }

    public void deleteEmployee(String username) {
        employeeRepository.deleteById(username);
    }
    
    public boolean isAlreadyRegistered(String username) {
    	return this.employeeRepository.existsByUsername(username);
    }
}

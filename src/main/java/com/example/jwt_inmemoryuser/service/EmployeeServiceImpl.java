package com.example.jwt_inmemoryuser.service;

import com.example.jwt_inmemoryuser.entity.Employee;
import com.example.jwt_inmemoryuser.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    @Override
    public Employee updateEmployee(Employee employee, Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        optionalEmployee.get().setName(employee.getName());
        optionalEmployee.get().setEmail(employee.getEmail());
        optionalEmployee.get().setDepartment(employee.getDepartment());
        optionalEmployee.get().setSalary(employee.getSalary());
        return employeeRepository.save(optionalEmployee.get());
    }
    @Override
    public void createEmployee(Employee employee) {
       employeeRepository.save(employee);
    }
    @Override
    public void deleteEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }
    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }
}
package com.example.jwt_inmemoryuser.service;
import com.example.jwt_inmemoryuser.entity.Employee;
import java.util.List;

public interface EmployeeService {

    List<Employee> getAllEmployees();
    Employee updateEmployee(Employee employee, Long id);
    void createEmployee(Employee employee);
    Employee deleteEmployeeById(Long id);
    Employee getEmployeeById(Long id);
}

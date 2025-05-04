package com.example.jwt_inmemoryuser.service;

import com.example.jwt_inmemoryuser.entity.Employee;
import java.util.List;

public interface EmployeeService {

    void createEmployee(Employee employee);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee updateEmployee(Employee employee, Long id);
    Employee deleteEmployeeById(Long id);

}

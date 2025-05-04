package com.example.jwt_inmemoryuser.repository;

import com.example.jwt_inmemoryuser.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
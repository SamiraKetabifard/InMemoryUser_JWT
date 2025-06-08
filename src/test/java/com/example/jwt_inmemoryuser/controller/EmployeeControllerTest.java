package com.example.jwt_inmemoryuser.controller;

import com.example.jwt_inmemoryuser.entity.Employee;
import com.example.jwt_inmemoryuser.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee(1L, "Samira", "samira@gmail.com", "50000", "IT");
    }

    @Test
    void addEmployee_shouldReturnCreatedStatus() {
        // given - no need to mock return value since controller doesn't use it
        doNothing().when(employeeService).createEmployee(any(Employee.class));

        // when
        ResponseEntity<String> response = employeeController.addEmployee(employee);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Employee added sucessfully");
        verify(employeeService).createEmployee(employee);
    }

    @Test
    void getAllEmployees_shouldReturnAllEmployees() {
        // given
        Employee emp2 = new Employee(2L, "Reza", "reza@gmail.com", "60000", "HR");
        List<Employee> employees = Arrays.asList(employee, emp2);
        given(employeeService.getAllEmployees()).willReturn(employees);

        // when
        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .hasSize(2)
                .extracting(Employee::getName)
                .containsExactlyInAnyOrder("Samira", "Reza");
    }

    @Test
    void getEmployee_withValidId_shouldReturnEmployee() {
        // given
        given(employeeService.getEmployeeById(1L)).willReturn(employee);

        // when
        ResponseEntity<Employee> response = employeeController.getEmployee(1L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(employee);
    }

    @Test
    void getEmployee_withInvalidId_shouldReturnNotFound() {
        // given
        given(employeeService.getEmployeeById(anyLong())).willReturn(null);

        // when
        ResponseEntity<Employee> response = employeeController.getEmployee(999L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void updateEmployee_withValidId_shouldReturnUpdatedEmployee() {
        // given
        Employee updatedDetails = new Employee(1L, "Mari", "mari@gmail.com", "55000", "Finance");
        given(employeeService.updateEmployee(any(Employee.class), anyLong())).willReturn(updatedDetails);

        // when
        ResponseEntity<Employee> response = employeeController.updateEmployee(1L, updatedDetails);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .extracting(Employee::getName, Employee::getDepartment)
                .containsExactly("Mari", "Finance");
    }

    @Test
    void deleteEmployee_withValidId_shouldReturnNoContent() {
        // given
        doNothing().when(employeeService).deleteEmployeeById(anyLong());

        // when
        ResponseEntity<Void> response = employeeController.deleteEmployee(1L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(employeeService).deleteEmployeeById(1L);
    }
}
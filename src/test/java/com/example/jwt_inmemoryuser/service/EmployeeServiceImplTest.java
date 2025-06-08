package com.example.jwt_inmemoryuser.service;

import com.example.jwt_inmemoryuser.entity.Employee;
import com.example.jwt_inmemoryuser.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee(1L, "Samira", "samira@gmail.com", "50000", "IT");
    }

    @Test
    void getAllEmployees_shouldReturnAllEmployees() {
        // given
        Employee emp2 = new Employee(2L, "Reza", "reza@gmail.com", "60000", "HR");
        given(employeeRepository.findAll()).willReturn(Arrays.asList(employee, emp2));

        // when
        List<Employee> employees = employeeService.getAllEmployees();

        // then
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getName).containsExactlyInAnyOrder("Samira", "Reza");
    }

    @Test
    void getEmployeeById_withValidId_shouldReturnEmployee() {
        // given
        given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

        // when
        Employee found = employeeService.getEmployeeById(1L);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Samira");
    }

    @Test
    void getEmployeeById_withInvalidId_shouldReturnNull() {
        // given
        given(employeeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        Employee found = employeeService.getEmployeeById(999L);

        // then
        assertThat(found).isNull();
    }

    @Test
    void createEmployee_shouldSaveEmployee() {
        // given
        given(employeeRepository.save(any(Employee.class))).willReturn(employee);

        // when
        employeeService.createEmployee(employee);

        // then
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void updateEmployee_withValidId_shouldUpdateEmployee() {
        // given
        Employee updatedDetails = new Employee(null, "Mari", "mari@gmail.com", "55000", "Finance");
        given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));
        given(employeeRepository.save(any(Employee.class))).willReturn(updatedDetails);

        // when
        Employee updated = employeeService.updateEmployee(updatedDetails, 1L);

        // then
        assertThat(updated.getName()).isEqualTo("Mari");
        assertThat(updated.getDepartment()).isEqualTo("Finance");
    }

    @Test
    void updateEmployee_withInvalidId_shouldThrowException() {
        // given
        Employee updatedDetails = new Employee(null, "Ali", "ali@gmail.com", "55000", "Finance");
        given(employeeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> employeeService.updateEmployee(updatedDetails, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Employee not found with id: 999");
    }

    @Test
    void deleteEmployee_withValidId_shouldDeleteEmployee() {
        // given
        given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(any(Employee.class));

        // when
        employeeService.deleteEmployeeById(1L);

        // then
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void deleteEmployee_withInvalidId_shouldThrowException() {
        // given
        given(employeeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> employeeService.deleteEmployeeById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Employee not found with id: 999");
    }
}

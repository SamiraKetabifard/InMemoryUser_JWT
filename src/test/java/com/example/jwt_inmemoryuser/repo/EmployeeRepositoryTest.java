package com.example.jwt_inmemoryuser.repo;

import com.example.jwt_inmemoryuser.entity.Employee;
import com.example.jwt_inmemoryuser.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void whenFindAll_thenReturnAllEmployees() {
        // given
        Employee emp1 = new Employee(null, "Samira", "samira@gmail.com", "50000", "IT");
        Employee emp2 = new Employee(null, "Reza", "reza@gmail.com", "60000", "HR");
        entityManager.persist(emp1);
        entityManager.persist(emp2);
        entityManager.flush();
        // when
        List<Employee> found = employeeRepository.findAll();
        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Employee::getName).containsExactlyInAnyOrder("Samira", "Reza");
    }
    @Test
    void whenFindById_thenReturnEmployee() {
        // given
        Employee emp = new Employee(null, "Mari", "mari@gmail.com", "50000", "IT");
        entityManager.persist(emp);
        entityManager.flush();
        // when
        Optional<Employee> found = employeeRepository.findById(emp.getId());
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Mari");
    }
    @Test
    void whenFindByInvalidId_thenReturnEmpty() {
        // when
        Optional<Employee> found = employeeRepository.findById(999L);
        // then
        assertThat(found).isEmpty();
    }
    @Test
    void whenSave_thenEmployeeIsPersisted() {
        // given
        Employee emp = new Employee(null, "Ali", "ali@gmail.com", "50000", "IT");
        // when
        Employee saved = employeeRepository.save(emp);
        // then
        assertThat(entityManager.find(Employee.class, saved.getId())).isNotNull();
        assertThat(saved.getName()).isEqualTo("Ali");
    }
    @Test
    void whenDelete_thenEmployeeIsRemoved() {
        // given
        Employee emp = new Employee(null, "Samira", "samira@gmail.com", "50000", "IT");
        entityManager.persist(emp);
        entityManager.flush();
        // when
        employeeRepository.delete(emp);
        // then
        assertThat(entityManager.find(Employee.class, emp.getId())).isNull();
    }
}

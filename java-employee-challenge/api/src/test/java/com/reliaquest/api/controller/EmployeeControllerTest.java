package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnEmployeeList_whenEmployeesExist() {
        List<Employee> mockEmployees = generateMockEmployees(1);
        when(employeeService.fetchEmployeesFromMockApi()).thenReturn(mockEmployees);

        ResponseEntity<List<Employee>> response = controller.getAllEmployees();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldReturnNotFound_whenNoEmployeesExist() {
        when(employeeService.fetchEmployeesFromMockApi()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Employee>> response = controller.getAllEmployees();

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldReturnEmployee_whenEmployeeExists() {
        Employee emp = createMockEmployee(1);
        when(employeeService.getEmployeeById("1")).thenReturn(Optional.of(emp));

        ResponseEntity<Employee> response = controller.getEmployeeById("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Employee1", response.getBody().getEmployeeName());
    }

    @Test
    void shouldReturnNoContent_whenEmployeeDoesNotExist() {
        when(employeeService.getEmployeeById("1")).thenReturn(Optional.empty());

        ResponseEntity<Employee> response = controller.getEmployeeById("1");

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void shouldDeleteEmployee_whenValidIdIsProvided() {
        UUID id = UUID.randomUUID();
        Employee emp = Employee.builder()
                .id(id)
                .employeeName("vipin")
                .employeeSalary(5000)
                .employeeAge(30)
                .employeeTitle("Engineer")
                .employeeEmail("vipin@example.com")
                .build();

        when(employeeService.getEmployeeById(id.toString())).thenReturn(Optional.of(emp));
        when(employeeService.deleteEmployeeByName("vipin")).thenReturn(true);

        ResponseEntity<String> response = controller.deleteEmployeeById(id.toString());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("vipin", response.getBody());
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingEmployee() {
        when(employeeService.getEmployeeById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = controller.deleteEmployeeById("1");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Employee not found", response.getBody());
    }

    @Test
    void shouldReturnMatchingEmployees_whenNameContainsSearchString() {
        List<Employee> mockList = generateMockEmployees(5);
        when(employeeService.fetchEmployeesFromMockApi()).thenReturn(mockList);

        ResponseEntity<List<Employee>> response = controller.getEmployeesByNameSearch("Employee1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldReturnEmptyList_whenNoNamesMatchSearchString() {
        List<Employee> mockList = generateMockEmployees(2);
        when(employeeService.fetchEmployeesFromMockApi()).thenReturn(mockList);

        ResponseEntity<List<Employee>> response = controller.getEmployeesByNameSearch("zzz");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldReturnHighestSalary_whenEmployeesExist() {
        List<Employee> mockList = generateMockEmployees(2);
        when(employeeService.fetchEmployeesFromMockApi()).thenReturn(mockList);

        ResponseEntity<Integer> response = controller.getHighestSalaryOfEmployees();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2000, response.getBody());
    }

    @Test
    void shouldReturnNotFound_whenGettingHighestSalaryAndNoEmployeesExist() {
        when(employeeService.fetchEmployeesFromMockApi()).thenReturn(Collections.emptyList());

        ResponseEntity<Integer> response = controller.getHighestSalaryOfEmployees();

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void shouldReturnTopTenHighestPaidEmployeeNames() {
        List<Employee> mockList = generateMockEmployees(20);

        when(employeeService.fetchEmployeesFromMockApi()).thenReturn(mockList);

        ResponseEntity<List<String>> response = controller.getTopTenHighestEarningEmployeeNames();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(10, response.getBody().size());
        assertEquals("Employee20", response.getBody().get(0)); // highest salary
    }

    @Test
    void shouldReturnNotFound_whenGettingTopTenAndNoEmployeesExist() {
        when(employeeService.fetchEmployeesFromMockApi()).thenReturn(Collections.emptyList());

        ResponseEntity<List<String>> response = controller.getTopTenHighestEarningEmployeeNames();

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldCreateEmployee_whenValidInputIsProvided() {
        CreateEmployeeInput input = createMockCreateEmployeeInput();
        Employee newEmployee = createMockEmployee(1);

        when(employeeService.createEmployee(input)).thenReturn(Optional.of(newEmployee));

        ResponseEntity<Employee> response = controller.createEmployee(input);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Employee1", response.getBody().getEmployeeName());
    }

    @Test
    void shouldReturnBadRequest_whenInvalidEmployeeDataIsProvided() {
        CreateEmployeeInput input = createMockCreateEmployeeInput();
        when(employeeService.createEmployee(input)).thenReturn(Optional.empty());

        ResponseEntity<Employee> response = controller.createEmployee(input);

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    private List<Employee> generateMockEmployees(int count) {
        List<Employee> employees = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            employees.add(Employee.builder()
                    .id(UUID.randomUUID())
                    .employeeName("Employee" + i)
                    .employeeSalary(1000 + (i * 500)) // Salary increases by 500 for each employee
                    .employeeAge(20 + i) // Age varies from 21 upwards
                    .employeeTitle("Title" + i)
                    .employeeEmail("employee" + i + "@example.com")
                    .build());
        }
        return employees;
    }

    private Employee createMockEmployee(int index) {
        return Employee.builder()
                .id(UUID.randomUUID())
                .employeeName("Employee" + index)
                .employeeSalary(1000 + (index * 500))
                .employeeAge(20 + index)
                .employeeTitle("Title" + index)
                .employeeEmail("employee" + index + "@example.com")
                .build();
    }

    private CreateEmployeeInput createMockCreateEmployeeInput() {
        return new CreateEmployeeInput("Employee1", 4000, 30, "Developer");
    }
}

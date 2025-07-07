package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeInput> {

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {

        List<Employee> employees = employeeService.fetchEmployeesFromMockApi();

        if (employees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }

        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        return employeeService
                .getEmployeeById(id)
                .map(employee -> ResponseEntity.ok(employee))
                .orElse(ResponseEntity.noContent().build());
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);

        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }

        String employeeName = employeeOpt.get().getEmployeeName();
        boolean deleted = employeeService.deleteEmployeeByName(employeeName);

        if (deleted) {
            // Return deleted employee name in the response data
            return ResponseEntity.ok(employeeName);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete employee");
        }
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        List<Employee> allEmployees = employeeService.fetchEmployeesFromMockApi();

        if (allEmployees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }

        List<Employee> matched = allEmployees.stream()
                .filter(e -> e.getEmployeeName() != null
                        && e.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                .toList();

        return ResponseEntity.ok(matched);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        List<Employee> allEmployees = employeeService.fetchEmployeesFromMockApi();

        if (allEmployees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Integer highestSalary =
                allEmployees.stream().mapToInt(e -> e.getEmployeeSalary()).max().orElse(0);
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<Employee> allEmployees = employeeService.fetchEmployeesFromMockApi();

        if (allEmployees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }

        List<String> top10Employees = allEmployees.stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .toList();
        return ResponseEntity.ok(top10Employees);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@RequestBody CreateEmployeeInput employeeInput) {
        Optional<Employee> createdEmployeeOpt = employeeService.createEmployee(employeeInput);

        return createdEmployeeOpt
                .map(emp -> ResponseEntity.ok(emp))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
    }
}

package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@Slf4j
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeInput> {

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("Calling  API: getAllEmployees -start");
        List<Employee> employees = employeeService.fetchEmployeesFromMockApi();
        log.info("Calling  API: getAllEmployees -end");
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
        log.info("Calling  API: deleteEmployeeById -start");
        Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);

        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }

        String employeeName = employeeOpt.get().getEmployeeName();
        boolean deleted = employeeService.deleteEmployeeByName(employeeName);
        log.info("Calling  API: deleteEmployeeById -end");
        if (deleted) {
            // Return deleted employee name in the response data
            return ResponseEntity.ok(employeeName);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete employee");
        }
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.info("Calling  API: getEmployeesByNameSearch -start");
        List<Employee> allEmployees = employeeService.fetchEmployeesFromMockApi();

        if (allEmployees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }

        List<Employee> matched = allEmployees.stream()
                .filter(e -> e.getEmployeeName() != null
                        && e.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                .toList();
        log.info("Calling  API: getEmployeesByNameSearch -end");
        return ResponseEntity.ok(matched);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Calling  API: getEmployeesByNameSearch -start");
        List<Employee> allEmployees = employeeService.fetchEmployeesFromMockApi();

        if (allEmployees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Integer highestSalary =
                allEmployees.stream().mapToInt(e -> e.getEmployeeSalary()).max().orElse(0);
        log.info("Calling  API: getHighestSalaryOfEmployees -end");
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Calling  API: getTopTenHighestEarningEmployeeNames -start");

        List<Employee> allEmployees = employeeService.fetchEmployeesFromMockApi();

        if (allEmployees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }

        List<String> top10Employees = allEmployees.stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .toList();
        log.info("Calling  API: getTopTenHighestEarningEmployeeNames -end");

        return ResponseEntity.ok(top10Employees);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@RequestBody CreateEmployeeInput employeeInput) {
        log.info("Calling  API: createEmployee -start");
        Optional<Employee> createdEmployeeOpt = employeeService.createEmployee(employeeInput);
        log.info("Calling  API: createEmployee -end");
        return createdEmployeeOpt
                .map(emp -> ResponseEntity.ok(emp))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
    }
}

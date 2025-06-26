package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeClient;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.DeleteEmployeeRequest;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Profile("default")
@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeClient employeeClient;
    @Override
    public List<Employee> getAllEmployees() {
        return employeeClient.fetchAllEmployees();
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        return getAllEmployees().stream()
                .filter(e -> e.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                .toList();
    }

    @Override
    public Employee getEmployeeById(String id) {
        Employee employee = employeeClient.fetchEmployeeById(id);
        if (employee == null || employee.getId() == null) {
            throw new EmployeeNotFoundException("Employee not found");
        }
        return employee;
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        return getAllEmployees().stream()
                .max(Comparator.comparingInt(Employee::getEmployeeSalary))
                .map(Employee::getEmployeeSalary)
                .orElse(0);
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        return getAllEmployees().stream()
                .sorted(Comparator.comparing(Employee::getEmployeeSalary).reversed())
                .map(Employee::getEmployeeName)
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public Employee createEmployee(CreateEmployeeRequest createRequest) {
        Employee employee = employeeClient.createEmployee(createRequest);
        if (employee == null || employee.getId() == null) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create an employee");
        }
        return employee;
    }

    @Override
    public String deleteEmployeeById(String id) {
        Employee employee = getEmployeeById(id);
        boolean deleted = employeeClient.deleteEmployee(new DeleteEmployeeRequest(employee.getEmployeeName()));
        if (!deleted) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete the employee");
        }
        return employee.getEmployeeName();
    }
}

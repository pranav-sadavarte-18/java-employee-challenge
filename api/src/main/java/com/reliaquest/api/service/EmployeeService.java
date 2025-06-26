package com.reliaquest.api.service;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.Employee;

import java.util.List;

 public interface EmployeeService {

     List<Employee> getAllEmployees();

     List<Employee> getEmployeesByNameSearch(String searchString);

     Employee getEmployeeById(String id);

     Integer getHighestSalaryOfEmployees();

     List<String> getTopTenHighestEarningEmployeeNames();

     Employee createEmployee(CreateEmployeeRequest employeeInput);

     String deleteEmployeeById(String id);
}

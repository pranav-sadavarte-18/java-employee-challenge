package com.reliaquest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllEmployees() throws Exception {
        List<Employee> list = List.of(new Employee("1", "John", "john@x.com", "Dev", 30, 5000));
        when(employeeService.getAllEmployees()).thenReturn(list);

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testGetEmployeesByNameSearch() throws Exception {
        List<Employee> list = List.of(new Employee("2", "Alice", "alice@x.com", "QA", 28, 6000));
        when(employeeService.getEmployeesByNameSearch("alice")).thenReturn(list);

        mockMvc.perform(get("/api/v1/employee/search/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].employee_name").value("Alice"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        Employee emp = new Employee("1", "John", "john@x.com", "Dev", 30, 5000);
        when(employeeService.getEmployeeById("1")).thenReturn(emp);

        mockMvc.perform(get("/api/v1/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("John"));
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById("123"))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Employee not found"));

        mockMvc.perform(get("/api/v1/employee/123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }

    @Test
    void testGetHighestSalary() throws Exception {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(9000);

        mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("9000"));
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(List.of("John", "Alice"));

        mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void testCreateEmployee_Valid() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", "QA", 25, 1000);
        Employee emp = new Employee("1", "Alice", "alice@x.com", "QA", 25, 6000);

        when(employeeService.createEmployee(any())).thenReturn(emp);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employee_name").value("Alice"));
    }

    @Test
    void testCreateEmployee_Invalid() throws Exception {
        String invalidJson = """
            {
              "name": "",
              "title": "",
              "age": 10
            }
        """;
        when(employeeService.createEmployee(any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "BAD REQUEST"));

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("BAD REQUEST"));
    }

    @Test
    void testDeleteEmployeeById_Success() throws Exception {
        when(employeeService.deleteEmployeeById("1")).thenReturn("John");

        mockMvc.perform(delete("/api/v1/employee/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("John"));
    }

    @Test
    void testDeleteEmployeeById_InternalError() throws Exception {
        when(employeeService.deleteEmployeeById("123"))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

        mockMvc.perform(delete("/api/v1/employee/123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }
}

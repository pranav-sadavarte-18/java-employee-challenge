package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeClient;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeClient employeeClient;

    @InjectMocks
    private EmployeeServiceImpl service;

    @Test
    void testGetAllEmployees() {
        List<Employee> list = List.of(new Employee("1", "John", "john@x.com", "Dev", 30, 5000));
        when(employeeClient.fetchAllEmployees()).thenReturn(list);

        assertEquals(1, service.getAllEmployees().size());
    }

    @Test
    void testGetEmployeesByNameSearch() {
        List<Employee> list = List.of(new Employee("1", "John", "john@x.com", "Dev", 30, 5000));
        when(employeeClient.fetchAllEmployees()).thenReturn(list);

        assertEquals(1, service.getEmployeesByNameSearch("john").size());
        assertEquals(0, service.getEmployeesByNameSearch("alice").size());
    }

    @Test
    void testGetEmployeeById_valid() {
        Employee emp = new Employee("1", "John", "john@x.com", "Dev", 30, 5000);
        when(employeeClient.fetchEmployeeById("1")).thenReturn(emp);

        assertEquals("John", service.getEmployeeById("1").getEmployeeName());
    }

    @Test
    void testGetEmployeeById_null() {
        when(employeeClient.fetchEmployeeById("1")).thenReturn(null);
        assertThrows(EmployeeNotFoundException.class, () -> service.getEmployeeById("1"));
    }

    @Test
    void testGetHighestSalary() {
        List<Employee> list = List.of(
                new Employee("1", "A", "a@x.com", "Dev", 30, 1000),
                new Employee("2", "B", "b@x.com", "QA", 28, 4000)
        );
        when(employeeClient.fetchAllEmployees()).thenReturn(list);

        assertEquals(4000, service.getHighestSalaryOfEmployees());
    }

    @Test
    void testGetHighestSalary_empty() {
        when(employeeClient.fetchAllEmployees()).thenReturn(List.of());
        assertEquals(0, service.getHighestSalaryOfEmployees());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        List<Employee> list = List.of(
                new Employee("1", "C", "c@x.com", "Dev", 30, 100),
                new Employee("2", "B", "b@x.com", "QA", 28, 500),
                new Employee("3", "A", "a@x.com", "QA", 25, 1000)
        );
        when(employeeClient.fetchAllEmployees()).thenReturn(list);

        List<String> result = service.getTopTenHighestEarningEmployeeNames();
        assertEquals(List.of("A", "B", "C"), result);
    }

    @Test
    void testCreateEmployee_success() {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", "QA", 25, 10000);
        Employee emp = new Employee("1", "Alice", "alice@x.com", "QA", 25, 5000);

        when(employeeClient.createEmployee(req)).thenReturn(emp);

        assertEquals("Alice", service.createEmployee(req).getEmployeeName());
    }

    @Test
    void testCreateEmployee_failure() {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", "QA", 25, 10000);
        when(employeeClient.createEmployee(req)).thenReturn(null);

        assertThrows(HttpClientErrorException.class, () -> service.createEmployee(req));
    }

    @Test
    void testDeleteEmployee_success() {
        Employee emp = new Employee("1", "John", "john@x.com", "Dev", 30, 5000);
        when(employeeClient.fetchEmployeeById("1")).thenReturn(emp);
        when(employeeClient.deleteEmployee(any())).thenReturn(true);

        assertEquals("John", service.deleteEmployeeById("1"));
    }

    @Test
    void testDeleteEmployee_failure() {
        Employee emp = new Employee("1", "John", "john@x.com", "Dev", 30, 5000);
        when(employeeClient.fetchEmployeeById("1")).thenReturn(emp);
        when(employeeClient.deleteEmployee(any())).thenReturn(false);

        assertThrows(HttpClientErrorException.class, () -> service.deleteEmployeeById("1"));
    }
}


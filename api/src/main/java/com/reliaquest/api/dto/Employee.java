package com.reliaquest.api.dto;

import lombok.Data;

@Data
public class Employee {
    private String id;
    private String employeeName;
    private String employeeEmail;
    private String employeeTitle;
    private Integer employeeAge;
    private Integer employeeSalary;
}

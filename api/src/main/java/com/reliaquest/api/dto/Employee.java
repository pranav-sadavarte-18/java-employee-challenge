package com.reliaquest.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private String id;
    private String employeeName;
    private String employeeEmail;
    private String employeeTitle;
    private Integer employeeAge;
    private Integer employeeSalary;
}

package com.reliaquest.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateEmployeeRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String title;

    @Min(16)
    @Max(75)
    private Integer age;

    @Positive
    private Integer salary;

}

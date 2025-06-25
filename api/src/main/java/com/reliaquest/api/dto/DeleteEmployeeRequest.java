package com.reliaquest.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteEmployeeRequest {
    @NotBlank
    private String name;
}

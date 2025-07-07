package com.reliaquest.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteEmployeeInput {

    @NotBlank
    private String name;
}

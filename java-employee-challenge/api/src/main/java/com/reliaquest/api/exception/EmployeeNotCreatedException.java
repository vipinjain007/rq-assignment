package com.reliaquest.api.exception;

import com.reliaquest.api.model.CreateEmployeeInput;

public class EmployeeNotCreatedException extends RuntimeException {
    private final CreateEmployeeInput input;

    public EmployeeNotCreatedException(String message, CreateEmployeeInput input, Throwable cause) {
        super(message, cause);
        this.input = input;
    }

    public CreateEmployeeInput getInput() {
        return input;
    }
}

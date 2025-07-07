package com.reliaquest.api.exception;

import com.reliaquest.api.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Object> handleEmployeeNotFound(EmployeeNotFoundException ex) {
        log.warn("Employee not found", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error(ex.getMessage()));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<Object> handleExternalApiException(ExternalApiException ex) {
        log.error("External API error", ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Response.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        log.error("Internal server error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(ex.getMessage()));
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Object> handleTooManyRequestsException(TooManyRequestsException ex) {
        log.error("Rate limit exceeded", ex);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Response.error(ex.getMessage()));
    }

    @ExceptionHandler(EmployeeNotCreatedException.class)
    public ResponseEntity<Object> handleEmployeeNotCreated(EmployeeNotCreatedException ex) {
        log.error("Employee creation error", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error(ex.getMessage()));
    }
}

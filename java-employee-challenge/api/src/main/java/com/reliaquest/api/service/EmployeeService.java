package com.reliaquest.api.service;

import com.reliaquest.api.exception.*;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;
import java.util.*;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EmployeeService {

    @Value("${mock.api.base-url}")
    private String mockApiBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    public List<Employee> fetchEmployeesFromMockApi() {
        String url = mockApiBaseUrl + "/employee";
        log.debug("Calling mock API: {}", url);

        try {
            ResponseEntity<Response<List<Employee>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<Response<List<Employee>>>() {});

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().data();
            } else {
                log.warn("Non-successful response status: {}", response.getStatusCode());
                return List.of();
            }
        } catch (HttpStatusCodeException ex) {
            HttpStatusCode status = ex.getStatusCode();
            log.error("HTTP error while calling mock API: status={}, message={}", status.value(), ex.getMessage());

            if (status.value() == HttpStatus.NOT_FOUND.value()) {
                // No employees found, return empty list
                return List.of();
            } else if (status.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new TooManyRequestsException("Rate limit exceeded while fetching employees", ex);
            } else {
                throw new ExternalApiException("Error fetching employees from mock API: " + status, ex);
            }
        } catch (RestClientException ex) {
            throw new ExternalApiException("Mock service unavailable:Failed to featch employees", ex);
        }
    }

    public Optional<Employee> getEmployeeById(String id) {
        String url = mockApiBaseUrl + "/employee/{id}";

        try {
            ResponseEntity<Response<Employee>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<Response<Employee>>() {}, id);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.ofNullable(response.getBody().data());
            }

        } catch (HttpClientErrorException.NotFound ex) {
            throw new EmployeeNotFoundException("Employee with ID " + id + " not found");

        } catch (HttpStatusCodeException ex) {
            HttpStatusCode status = ex.getStatusCode();

            if (status.value() == HttpStatus.NOT_FOUND.value()) {
                throw new EmployeeNotFoundException("Employee with ID " + id + " not found");
            } else if (status.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new TooManyRequestsException("Rate limit exceeded", ex);
            } else {
                throw new ExternalApiException("HTTP error from mock API: " + status, ex);
            }

        } catch (RestClientException ex) {
            throw new ExternalApiException("Mock service unavailable:Failed to fetch employee data", ex);
        }

        return Optional.empty();
    }

    public boolean deleteEmployeeByName(String name) {
        String deleteUrl = mockApiBaseUrl + "/employee"; // Replace with actual delete endpoint

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("name", name);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(deleteUrl, HttpMethod.DELETE, requestEntity, String.class);

            return response.getStatusCode().is2xxSuccessful();

        } catch (HttpClientErrorException.NotFound ex) {
            throw new EmployeeNotFoundException("Employee with name " + name + " not found");

        } catch (HttpStatusCodeException ex) {
            HttpStatusCode status = ex.getStatusCode();

            if (status.value() == HttpStatus.NOT_FOUND.value()) {
                throw new EmployeeNotFoundException("Employee with name " + name + " not found");
            } else if (status.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new TooManyRequestsException("Rate limit exceeded", ex);
            } else {
                throw new ExternalApiException("HTTP error from mock API: " + status, ex);
            }

        } catch (RestClientException ex) {
            throw new ExternalApiException("Mock service unavailable:Failed to delete employee name:" + name, ex);
        }
    }

    public Optional<Employee> createEmployee(CreateEmployeeInput request) {
        String url = mockApiBaseUrl + "/employee";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateEmployeeInput> httpEntity = new HttpEntity<>(request, headers);
        try {
            ResponseEntity<Response<Employee>> response = restTemplate.exchange(
                    url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<Response<Employee>>() {});

            return Optional.ofNullable(response.getBody()).map(Response::data);
        } catch (HttpClientErrorException.NotFound ex) {
            // Employee not found
            throw new EmployeeNotCreatedException("Failed to create employee", request, ex);

        } catch (HttpStatusCodeException ex) {
            HttpStatusCode status = ex.getStatusCode();

            if (status.value() == HttpStatus.NOT_FOUND.value()) {
                throw new EmployeeNotCreatedException("Failed to create employee", request, ex);
            } else if (status.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new TooManyRequestsException("Rate limit exceeded", ex);
            } else {
                throw new ExternalApiException("HTTP error from mock API: " + status, ex);
            }

        } catch (RestClientException ex) {
            throw new ExternalApiException(
                    "Mock service unavailable:Failed to create employee name: :" + request.getName(), ex);
        }
    }
}

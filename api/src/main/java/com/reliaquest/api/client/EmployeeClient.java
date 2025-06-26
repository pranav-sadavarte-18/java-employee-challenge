package com.reliaquest.api.client;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.DeleteEmployeeRequest;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmployeeClient {
    private final RestTemplate restTemplate;

    @Value("${employee.client.base.url}")
    private String BASE_URL;

    public List<Employee> fetchAllEmployees() {
        ResponseEntity<Response<List<Employee>>> responseEntity = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return Optional.ofNullable(processResponse(responseEntity)).orElse(List.of());
    }

    public Employee fetchEmployeeById(String id) {
        ResponseEntity<Response<Employee>> responseEntity = restTemplate.exchange(
                BASE_URL + "/" + UUID.fromString(id),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Response<Employee>>() {
                }
        );

        return processResponse(responseEntity);
    }

    public Employee createEmployee(CreateEmployeeRequest createRequest) {
        HttpEntity<CreateEmployeeRequest> request = new HttpEntity<>(createRequest);
        ResponseEntity<Response<Employee>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );
        return processResponse(response);
    }

    public boolean deleteEmployee(DeleteEmployeeRequest deleteRequest) {
        HttpEntity<DeleteEmployeeRequest> request = new HttpEntity<>(deleteRequest);
        ResponseEntity<Response<Boolean>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>() {}
        );
        return processResponse(response);
    }


    private <T> T processResponse(ResponseEntity<Response<T>> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Response<T> response = responseEntity.getBody();
            if (response != null && response.status() == Response.Status.HANDLED) {
                return response.data();
            }
        }
        log.error("Failed to execute API request. Status: {} and message : {}", responseEntity.getStatusCode(), responseEntity.getBody());
        throw new HttpClientErrorException(
                responseEntity.getStatusCode(),
                "Failed to get a valid response from server"
        );
    }

}

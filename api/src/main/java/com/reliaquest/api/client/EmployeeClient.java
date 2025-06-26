package com.reliaquest.api.client;

import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmployeeClient {
    private final RestTemplate restTemplate;

    @Value("${employee.client.base.url}")
    private String BASE_URL;

    public List<Employee> getAllEmployees() {
        ResponseEntity<Response<List<Employee>>> responseEntity = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return Optional.ofNullable(processResponse(responseEntity)).orElse(List.of());
    }


    private <T> T processResponse(ResponseEntity<Response<T>> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Response<T> response = responseEntity.getBody();
            if (response != null && response.status() == Response.Status.HANDLED) {
                return response.data();
            }
        }
        log.error("Failed to fetch data using API with Status: {} and message : {}", responseEntity.getStatusCode(), responseEntity.getBody());
        throw new HttpClientErrorException(
                responseEntity.getStatusCode(),
                "Failed to get a valid response from server"
        );
    }

}

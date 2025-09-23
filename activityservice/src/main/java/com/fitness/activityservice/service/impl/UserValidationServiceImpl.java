package com.fitness.activityservice.service.impl;

import com.fitness.activityservice.service.IUserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements IUserValidationService {
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {
        try {
            return Boolean.TRUE.equals(userServiceWebClient.get()
                    .uri("/api/user/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("User not found:" + userId);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Invalid request");
            }
        }
        return false;
    }

}

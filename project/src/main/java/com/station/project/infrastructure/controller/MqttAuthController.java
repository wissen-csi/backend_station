package com.station.project.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.station.project.application.services.AuthServices;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MqttAuthController {

    private final AuthServices authServices;

    @PostMapping("/api/mqtt/auth")
    public ResponseEntity<Void> authenticate(@RequestBody CredentialsRequest request) {
        if (request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            authServices.validateCredentials(request.username(), request.password());
            return ResponseEntity.ok().build();
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public record CredentialsRequest(String username, String password) {
    }
}

package com.station.project.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.station.project.application.services.TokensMQTTService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MqttAuthController {

    private final TokensMQTTService tokensMQTTService;

    @PostMapping("/api/mqtt/auth")
    public ResponseEntity<Void> authenticate(@RequestBody(required = false) AuthRequest request) {
        if (request == null || request.password() == null || request.password().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (tokensMQTTService.isTokenActive(request.password())) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    public record AuthRequest(String username, String password) {
    }
}
package com.station.project.infrastructure.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.station.project.application.services.TokensMQTTService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MqttAuthController {

    private final TokensMQTTService tokensMQTTService;

    @PostMapping("/api/mqtt/auth")
    public ResponseEntity<Void> authenticate(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody(required = false) GoAuthUserRequest request) {
        String token = extractToken(authorization);
        if (token == null || !tokensMQTTService.isTokenActive(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }

    static String extractToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        return authorization.startsWith("Bearer ")
                ? authorization.substring("Bearer ".length()).trim()
                : authorization.trim();
    }

    public record GoAuthUserRequest(String username, String password) {
    }
}

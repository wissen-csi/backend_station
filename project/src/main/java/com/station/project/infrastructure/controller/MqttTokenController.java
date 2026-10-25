package com.station.project.infrastructure.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.station.project.application.port.in.FindByNameUserAuth;
import com.station.project.application.port.in.TokenMQTT;
import com.station.project.domain.model.UserAuth;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/mqtt/tokens")
@RequiredArgsConstructor
public class MqttTokenController {

    private final TokenMQTT tokenMQTT;
    private final FindByNameUserAuth findByNameUserAuth;

    @PostMapping("/{audience}")
    @PermitAll 
    public MqttTokenResponse issue(
            @PathVariable String audience,
            Authentication authentication) {
        UserAuth user = findByNameUserAuth.findByUserName(authentication.getName());
        String token = switch (audience.toLowerCase()) {
            case "front" -> tokenMQTT.tokenFront(user);
            case "admin" -> tokenMQTT.tokenAdmin(user);
            case "boya" -> tokenMQTT.tokenBoya(user);
            default -> throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Unknown MQTT token audience");
        };
        return new MqttTokenResponse(token);
    }

    public record MqttTokenResponse(String token) {
    }
}

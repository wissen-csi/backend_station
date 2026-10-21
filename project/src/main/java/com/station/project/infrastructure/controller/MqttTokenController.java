package com.station.project.infrastructure.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.station.project.application.services.TokensMQTTService;
import com.station.project.application.services.UserAuthService;
import com.station.project.domain.enumerations.Role;
import com.station.project.domain.model.UserAuth;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/mqtt/tokens")
@RequiredArgsConstructor
public class MqttTokenController {

    private final TokensMQTTService tokensMQTTService;
    private final UserAuthService userAuthService;

    @PostMapping("/{audience}")
    public MqttTokenResponse issue(
            @PathVariable String audience,
            Authentication authentication) {
        UserAuth user = userAuthService.findByUserName(authentication.getName());
        String token = switch (audience.toLowerCase()) {
            case "front" -> tokensMQTTService.tokenFront(user);
            case "admin" -> {
                requireRole(user, Role.ADMIN);
                yield tokensMQTTService.tokenAdmin(user);
            }
            case "boya" -> {
                requireRole(user, Role.BOYA);
                yield tokensMQTTService.tokenBoya(user);
            }
            default -> throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Unknown MQTT token audience");
        };
        return new MqttTokenResponse(token);
    }

    private void requireRole(UserAuth user, Role role) {
        if (user.role() != role) {
            throw new AccessDeniedException("Insufficient role to issue this MQTT token");
        }
    }

    public record MqttTokenResponse(String token) {
    }
}

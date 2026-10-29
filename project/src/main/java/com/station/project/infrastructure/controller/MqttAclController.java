package com.station.project.infrastructure.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.station.project.application.services.TokensMQTTService;
import com.station.project.infrastructure.components.MqttBrokerProperties;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MqttAclController {

    private final TokensMQTTService tokensMQTTService;
    private final MqttBrokerProperties mqttBrokerProperties;

    @PostMapping("/api/mqtt/acl")
    public ResponseEntity<Void> authorize(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody(required = false) AclRequest request) {
        if (request == null || request.topic() == null || request.topic().isBlank()
                || request.acc() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String token = MqttAuthController.extractToken(authorization);
        if (token == null || !tokensMQTTService.isAuthorized(
                token,
                request.topic(),
                request.acc(),
                mqttBrokerProperties.getTopic())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }

    public record AclRequest(String topic, String clientid, Integer acc) {
    }
}

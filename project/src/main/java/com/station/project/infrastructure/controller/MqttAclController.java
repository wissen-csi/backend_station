package com.station.project.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.station.project.application.services.TokensMQTTService;
import com.station.project.infrastructure.components.MqttBrokerProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j 
@RequiredArgsConstructor
public class MqttAclController {
    private final TokensMQTTService tokensMQTTService;
    private final MqttBrokerProperties mqttBrokerProperties;
@PostMapping("/api/mqtt/acl")
    public ResponseEntity<Void> authorize(@RequestBody(required = false) AclRequest request) {
        
        if (request == null || request.username() == null || request.topic() == null || request.acc() == null) {
            log.warn("Petición ACL rechazada por faltar datos clave: {}", request);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean isAllowed = tokensMQTTService.checkAcl(request.username(), request.topic(), request.acc());

        if (isAllowed) {
            return ResponseEntity.ok().build(); 
        } else {
            log.warn("ACL Denegado para usuario: {} en tópico: {}", request.username(), request.topic());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
    }

    public record AclRequest(String username, String password, String clientid, String topic, Integer acc) {
    }
}
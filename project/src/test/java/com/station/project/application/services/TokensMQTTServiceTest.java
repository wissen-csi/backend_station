package com.station.project.application.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.station.project.application.port.out.JwtPort;
import com.station.project.application.port.out.TokenRepositoryPort;
import com.station.project.domain.enumerations.TokenType;
import com.station.project.domain.model.Token;

class TokensMQTTServiceTest {

    private JwtPort jwtPort;
    private TokenRepositoryPort tokenRepositoryPort;
    private TokensMQTTService tokensMQTTService;

    @BeforeEach
    void setUp() {
        jwtPort = mock(JwtPort.class);
        tokenRepositoryPort = mock(TokenRepositoryPort.class);
        tokensMQTTService = new TokensMQTTService(jwtPort, tokenRepositoryPort);
    }

    @Test
    void acceptsValidActiveMqttToken() {
        when(jwtPort.isTokenExpired("valid-token")).thenReturn(false);
        when(jwtPort.extractUserName("valid-token")).thenReturn("frontend");
        when(tokenRepositoryPort.findByTokenAndTokenTypeAndExpiredIsFalseAndRevokedIsFalse(
                "valid-token", TokenType.MQTT))
                .thenReturn(Optional.of(Token.builder().token("valid-token").build()));

        assertTrue(tokensMQTTService.isTokenActive("valid-token"));
    }

    @Test
    void rejectsTokenThatIsNotActiveInRepository() {
        when(jwtPort.isTokenExpired("revoked-token")).thenReturn(false);
        when(jwtPort.extractUserName("revoked-token")).thenReturn("frontend");
        when(tokenRepositoryPort.findByTokenAndTokenTypeAndExpiredIsFalseAndRevokedIsFalse(
                "revoked-token", TokenType.MQTT))
                .thenReturn(Optional.empty());

        assertFalse(tokensMQTTService.isTokenActive("revoked-token"));
    }

    @Test
    void restrictsAudiencePermissionsToConfiguredTopic() {
        when(jwtPort.isTokenExpired("frontend-token")).thenReturn(false);
        when(jwtPort.extractUserName("frontend-token")).thenReturn("frontend");
        when(tokenRepositoryPort.findByTokenAndTokenTypeAndExpiredIsFalseAndRevokedIsFalse(
                "frontend-token", TokenType.MQTT))
                .thenReturn(Optional.of(Token.builder().token("frontend-token").build()));

        assertTrue(tokensMQTTService.isAuthorized(
                "frontend-token", "test/topic", 1, "test/topic"));
        assertTrue(tokensMQTTService.isAuthorized(
                "frontend-token", "test/topic", 4, "test/topic"));
        assertFalse(tokensMQTTService.isAuthorized(
                "frontend-token", "test/topic", 2, "test/topic"));
        assertFalse(tokensMQTTService.isAuthorized(
                "frontend-token", "other/topic", 1, "test/topic"));
    }

    @Test
    void acceptsBrokerSystemTokenForConfiguredTopic() {
        when(jwtPort.isTokenExpired("system-token")).thenReturn(false);
        when(jwtPort.extractUserName("system-token")).thenReturn("broker-system");

        assertTrue(tokensMQTTService.isTokenActive("system-token"));
        assertTrue(tokensMQTTService.isAuthorized(
                "system-token", "test/topic", 2, "test/topic"));
    }
}

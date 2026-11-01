package com.station.project.application.services;

import java.util.Set;

import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;

import com.station.project.application.port.in.TokenMQTT;
import com.station.project.application.port.out.JwtPort;
import com.station.project.application.port.out.TokenRepositoryPort;
import com.station.project.domain.enumerations.TokenType;
import com.station.project.domain.model.Token;
import com.station.project.domain.model.UserAuth;

import lombok.AllArgsConstructor;
@Service 
@AllArgsConstructor 
public class TokensMQTTService implements TokenMQTT {

    private static final String SYSTEM_SUBJECT = "broker-system";
    private static final Set<Integer> READ_ACCESS = Set.of(1, 4);
    private static final Set<Integer> WRITE_ACCESS = Set.of(2);
    private static final Set<Integer> ADMIN_ACCESS = Set.of(1, 2, 3, 4);

    private final JwtPort jwtPort;
    private final TokenRepositoryPort tokenRepositoryPort;
    @Override
    public String tokenFront(UserAuth user) {
        revokedAllMQTT();
        String tokenRaw = jwtPort.generateMQTTFront();
        Token token = Token.builder()
                .token(tokenRaw)
                .revoked(false)
                .expired(false)
                .user(user)
                .tokenType(TokenType.MQTT)
                .build();
        tokenRepositoryPort.save(token);
        return tokenRaw;
    }

    @Override
    public String tokenAdmin(UserAuth userAuth) {
        revokedAllMQTT();
        String tokenRaw = jwtPort.generateMQTTAdmin();
        Token token = Token.builder()
                .token(tokenRaw)
                .revoked(false)
                .expired(false)
                .user(userAuth)
                .tokenType(TokenType.MQTT)
                .build();
        tokenRepositoryPort.save(token);
        return tokenRaw;
    }

    @Override
    public String tokenBoya(UserAuth userAuth) {
        revokedAllMQTT();
        String tokenRaw = jwtPort.generateMQTTBoya();
        Token token = Token.builder()
                .token(tokenRaw)
                .revoked(false)
                .expired(false)
                .user(userAuth)
                .tokenType(TokenType.MQTT)
                .build();
        tokenRepositoryPort.save(token);
        return tokenRaw;
    }

    private void revokedAllMQTT() {
        var list = tokenRepositoryPort.findAllByTokenTypeAndExpiredIsFalseAndRevokedIsFalse(TokenType.MQTT);
        tokenRepositoryPort.saveAll(list.stream().map((token) -> Token.builder()
                .id(token.id())
                .token(token.token())
                .revoked(true)
                .expired(true)
                .user(token.user())
                .tokenType(token.tokenType())
                .build()).toList());
    }

    @Override
    public String systemToken() {
        return jwtPort.generateMQTTSystem();
    }

    @Override
    public boolean isTokenActive(String token) {
        return true;
    }

@Override
public boolean isAuthorized(String token, String topic, int access, String configuredTopic) {
    if (topic == null || topic.isBlank()) {
        return false;
    }

    String subject = validatedSubject(token);
    if (subject == null) {
        return false;
    }

    if (SYSTEM_SUBJECT.equals(subject) || "system".equals(subject)) {
        return true; // Permiso total (Lectura y Escritura) en cualquier tópico
    }

    if (tokenRepositoryPort.findByTokenAndTokenTypeAndExpiredIsFalseAndRevokedIsFalse(
            token, TokenType.MQTT).isEmpty()) {
        return false;
    }

    return switch (subject) {
        case "admin" -> true;
        case "frontend" -> access == 1 && matchesTopicFilter("sensores/#", topic);
        case "boya-001" -> access == 2 && matchesTopicFilter("sensores/#", topic);
        default -> false;
    };
}

    private String validatedSubject(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            if (jwtPort.isTokenExpired(token)) {
                return null;
            }
            return jwtPort.extractUserName(token);
        } catch (JwtException | IllegalArgumentException exception) {
            return null;
        }
    }

    private boolean matchesTopicFilter(String filter, String topic) {
        String[] filterLevels = filter.split("/", -1);
        String[] topicLevels = topic.split("/", -1);
        for (int index = 0; index < filterLevels.length; index++) {
            String filterLevel = filterLevels[index];
            if ("#".equals(filterLevel)) {
                return index == filterLevels.length - 1;
            }
            if (index >= topicLevels.length) {
                return false;
            }
            if (!"+".equals(filterLevel) && !filterLevel.equals(topicLevels[index])) {
                return false;
            }
        }
        return filterLevels.length == topicLevels.length;
    }
    public boolean checkAcl(String username, String topic, Integer acc) {
        if ("system".equals(username)) {
            return true;
        }

        if ("boya-001".equals(username)) {
            if (acc == 2 && topic.startsWith("sensores/")) {
                return true;
            }
        }

        return false;
    }
}

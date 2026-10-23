package com.station.project.application.services;

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

    private JwtPort jwtPort;
    private TokenRepositoryPort tokenRepositoryPort;
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
        return jwtPort.generateMQTTAdmin();
    }

}

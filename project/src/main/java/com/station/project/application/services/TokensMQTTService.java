package com.station.project.application.services;

import com.station.project.application.port.in.TokenMQTT;
import com.station.project.application.port.out.TokenRepositoryPort;
import com.station.project.domain.enumerations.TokenType;
import com.station.project.domain.model.Token;
import com.station.project.domain.model.UserAuth;
import com.station.project.infrastructure.adapters.GeneratorTokenAdapter;

import lombok.AllArgsConstructor;
@AllArgsConstructor 
public class TokensMQTTService implements TokenMQTT {

    private GeneratorTokenAdapter generatorTokenAdapter;
    private TokenRepositoryPort tokenRepositoryPort;
    @Override
    public String tokenFront(UserAuth user) {
        String tokenRaw = generatorTokenAdapter.generateMQTTFront();
        Token token =Token.builder()
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
        String tokenRaw = generatorTokenAdapter.generateMQTTAdmin();
        Token token =Token.builder()
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
        String tokenRaw = generatorTokenAdapter.generateMQTTBoya();
        Token token =Token.builder()
        .token(tokenRaw)
        .revoked(false)
        .expired(false)
        .user(userAuth)
        .tokenType(TokenType.MQTT)
        .build();
        tokenRepositoryPort.save(token);
        return tokenRaw;
    }

}

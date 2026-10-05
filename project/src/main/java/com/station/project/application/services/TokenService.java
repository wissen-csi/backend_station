package com.station.project.application.services;

import java.util.List;

import com.station.project.application.port.in.CreateToken;
import com.station.project.application.port.in.RevokedAll;
import com.station.project.application.port.out.TokenRepositoryPort;
import com.station.project.domain.enumerations.TokenType;
import com.station.project.domain.model.Token;
import com.station.project.domain.model.UserAuth;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenService implements CreateToken, RevokedAll {
    private TokenRepositoryPort tokenRepositoryPort;

    @Override
    public Token save(Token token) {
        return tokenRepositoryPort.save(token);
    }

    @Override
    public void revokedAll(UserAuth user) {
        List<Token> list = tokenRepositoryPort.findAllByUserAuthIdAndExpiredIsFalseAndRevokedIsFalse(user);
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
    public void revokedAllMQTT(UserAuth user) {
        List<Token> list = tokenRepositoryPort.findAllByTokenTypeAndExpiredIsFalseAndRevokedIsFalse(TokenType.MQTT);
        tokenRepositoryPort.saveAll(list.stream().map((token) -> Token.builder()
                .id(token.id())
                .token(token.token())
                .revoked(true)
                .expired(true)
                .user(token.user())
                .tokenType(token.tokenType())
                .build()).toList());
    }

}

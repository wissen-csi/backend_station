package com.station.project.application.port.out;

import java.util.List;

import com.station.project.domain.enumerations.TokenType;
import com.station.project.domain.model.Token;
import com.station.project.domain.model.UserAuth;

public interface TokenRepositoryPort {
    Token save(Token token);

    List<Token> findAllByUserAuthIdAndExpiredIsFalseAndRevokedIsFalse(UserAuth user);

    List<Token> findAll();

    List<Token> saveAll(List<Token> list);

    List<Token> findAllByTokenTypeAndExpiredIsFalseAndRevokedIsFalse(TokenType tokenType);
}

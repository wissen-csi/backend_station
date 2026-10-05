package com.station.project.application.port.out;

import java.util.List;

import com.station.project.domain.enumerations.TokenType;
import com.station.project.domain.model.Token;
import com.station.project.domain.model.UserAuth;

public interface TokenRepositoryPort {
public Token save(Token token);
public List<Token> findAllByUserAuthIdAndExpiredIsFalseAndRevokedIsFalse(UserAuth user);
public List<Token> findAll();
public List<Token> saveAll(List<Token> list);
public List<Token> findAllByTokenTypeAndExpiredIsFalseAndRevokedIsFalse(TokenType tokenType);
}

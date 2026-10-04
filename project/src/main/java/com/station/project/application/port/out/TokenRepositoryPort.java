package com.station.project.application.port.out;

import java.util.List;

import com.station.project.domain.model.Token;
import com.station.project.domain.model.UserAuth;

public interface TokenRepositoryPort {
public Token save(Token token);
public List<Token> findAllValidIsFalseOrRevokedIsFalseByUserId(UserAuth user);
}

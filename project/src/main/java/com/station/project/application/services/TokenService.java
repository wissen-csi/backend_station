package com.station.project.application.services;

import com.station.project.application.port.in.CreateToken;
import com.station.project.application.port.out.TokenRepositoryPort;
import com.station.project.domain.model.Token;

import lombok.AllArgsConstructor;
@AllArgsConstructor 
public class TokenService implements CreateToken {
    private  TokenRepositoryPort tokenRepositoryPort;
    @Override
    public Token save(Token token) {
        return tokenRepositoryPort.save(token);
    }

}

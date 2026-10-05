package com.station.project.application.services;

import org.yaml.snakeyaml.util.Tuple;

import com.station.project.application.port.in.Login;
import com.station.project.application.port.out.AuthenticationPort;
import com.station.project.application.port.out.GeneratorTokenPort;
import com.station.project.domain.enumerations.TokenType;
import com.station.project.domain.model.Token;
import com.station.project.domain.model.UserAuth;

import lombok.AllArgsConstructor;
@AllArgsConstructor 
public class AuthServices implements Login {

    private TokenService tokenService;
    private GeneratorTokenPort generatorTokenPort;
    private  AuthenticationPort authenticationPort;
    private UserAuthService service;
    @Override
    public Tuple<String, String> login(String userName, String password) {
        authenticationPort.auth(userName, password);
        UserAuth user = service.findByUserName(userName);
        String jwtToken = generatorTokenPort.generateToken(user);
        String jwtTokenRefresh = generatorTokenPort.generateRefreshToken(user);
        Token jwt = Token.builder()
        .token(jwtToken)
        .revoked(false)
        .expired(false)
        .user(user)
        .tokenType(TokenType.BARRER)
        .build();
        Token refresh = Token.builder()
        .token(jwtTokenRefresh)
        .revoked(false)
        .expired(false)
        .user(user)
        .tokenType(TokenType.BARRER)
        .build();
        tokenService.revokedAll(user);
        tokenService.save(refresh);
        tokenService.save(jwt);
        return new Tuple<String,String>(jwtToken, jwtTokenRefresh);

    }


}

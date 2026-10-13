package com.station.project.application.services;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.Tuple;

import com.station.project.application.port.in.Login;
import com.station.project.application.port.in.Refresh;
import com.station.project.application.port.out.AuthenticationPort;
import com.station.project.application.port.out.JwtPort;
import com.station.project.domain.enumerations.TokenType;
import com.station.project.domain.model.Token;
import com.station.project.domain.model.UserAuth;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServices implements Login, Refresh {

    private final TokenService tokenService;
    private final JwtPort jwtPort;
    private final AuthenticationPort authenticationPort;
    private final UserAuthService userAuthService;

    @Override
    public Tuple<String, String> login(String userName, String password) {
        authenticationPort.auth(userName, password);
        UserAuth user = userAuthService.findByUserName(userName);
        String jwtToken = jwtPort.generateToken(user);
        String jwtTokenRefresh = jwtPort.generateRefreshToken(user);
        Token jwt = Token.builder()
        .token(jwtToken)
        .revoked(false)
        .expired(false)
        .user(user)
        .tokenType(TokenType.ACCESS)
        .build();
        Token refresh = Token.builder()
        .token(jwtTokenRefresh)
        .revoked(false)
        .expired(false)
        .user(user)
        .tokenType(TokenType.REFRESH)
        .build();
        tokenService.revokedAll(user);
        tokenService.save(refresh);
        tokenService.save(jwt);
        return new Tuple<String,String>(jwtToken, jwtTokenRefresh);

    }
    @Override
    public Tuple<String, String> refresh(String authHeader) throws IllegalArgumentException {
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            throw new IllegalArgumentException();
        }
        String refreshToken = authHeader.substring(7);
        String userName = jwtPort.extractUserName(refreshToken);
        if(userName==null){
            throw new IllegalArgumentException();
        }
        UserAuth userAuth = userAuthService.findByUserName(userName);
        if(!jwtPort.isTokenValid(refreshToken, userAuth))
            throw new IllegalArgumentException();

        String newToken = jwtPort.generateToken(userAuth);
        tokenService.revokedAll(userAuth);
        Token jwt = Token.builder()
        .token(newToken)
        .revoked(false)
        .expired(false)
        .user(userAuth)
        .tokenType(TokenType.ACCESS)
        .build();
        tokenService.save(jwt);
        return  new Tuple<String,String>(userName, newToken);
    }


}

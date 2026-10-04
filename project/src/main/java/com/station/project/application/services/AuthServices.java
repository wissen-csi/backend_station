package com.station.project.application.services;

import org.yaml.snakeyaml.util.Tuple;

import com.station.project.application.port.in.Login;
import com.station.project.application.port.out.AuthenticationPort;
import com.station.project.application.port.out.GeneratorTokenPort;
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
        return null;

    }


}

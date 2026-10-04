package com.station.project.infrastructure.adapters;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.station.project.application.port.out.AuthenticationPort;

import lombok.RequiredArgsConstructor;
@Component 
@RequiredArgsConstructor 
public class AuthenticationAdapter implements AuthenticationPort {
    private  final AuthenticationManager authenticationManager;
    @Override
    public void auth(String userName, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
    }

}

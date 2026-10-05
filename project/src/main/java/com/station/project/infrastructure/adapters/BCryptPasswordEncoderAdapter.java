package com.station.project.infrastructure.adapters;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.station.project.application.port.out.PasswordEnconderPort;

import lombok.AllArgsConstructor;

 
@AllArgsConstructor 
public class BCryptPasswordEncoderAdapter implements PasswordEnconderPort {
    private PasswordEncoder passwordEncoder;

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword,encodedPassword);
    }

}

package com.station.project.application.port.out;

import org.yaml.snakeyaml.util.Tuple;

import com.station.project.domain.model.UserAuth;

public interface JwtPort {
    public String generate(String id, UserAuth user, long expiration);
    public String generateToken(UserAuth user);
    public String generateRefreshToken(UserAuth user);
    public String extractUserName(String token);
    public String generateMQTTFront();
    public String generateMQTTAdmin();
    public String generateMQTTBoya();
    public String generateMQTTSystem();
    public boolean isTokenValid(String token, UserAuth userAuth);
    public boolean isTokenExpired(String token);
}

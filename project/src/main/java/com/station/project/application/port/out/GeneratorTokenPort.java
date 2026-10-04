package com.station.project.application.port.out;

import com.station.project.domain.model.UserAuth;

public interface GeneratorTokenPort {
    public String generate(String id, UserAuth user, long expiration);
    public String generateToken(UserAuth user);
    public String generateRefreshToken(UserAuth user);
}

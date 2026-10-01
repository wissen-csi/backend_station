package com.station.project.application.port.out;

import com.station.project.domain.model.UserAuth;

public interface GeneratorTokenPort {
    public String generate(String id, UserAuth user, long expiration);
}

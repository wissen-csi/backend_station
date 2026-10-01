package com.station.project.application.port.in;

import com.station.project.domain.model.UserAuth;

public interface BuildToken {
public String buildToken(UserAuth user, long expiration);
}

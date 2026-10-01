package com.station.project.application.port.in;

import com.station.project.domain.model.UserAuth;

public interface GenerateRefreshToken {
public String generateRefreshToken(UserAuth user);
}

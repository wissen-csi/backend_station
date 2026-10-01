package com.station.project.application.port.in;

import com.station.project.domain.model.UserAuth;

public interface GenerateToken {
    public String generate(UserAuth user);

}

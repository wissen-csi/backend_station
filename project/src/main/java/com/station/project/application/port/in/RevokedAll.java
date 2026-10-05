package com.station.project.application.port.in;

import com.station.project.domain.model.UserAuth;

public interface RevokedAll {
    public void revokedAll(UserAuth user);
}

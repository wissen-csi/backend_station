package com.station.project.application.port.in;

import com.station.project.domain.model.UserAuth;

public interface findByNameUserAuth {
    public UserAuth findByUserName(String userName);
}

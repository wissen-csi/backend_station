package com.station.project.application.port.in;

import com.station.project.domain.model.UserAuth;

public interface FindByNameUserAuth {
    public UserAuth findByUserName(String userName);
}

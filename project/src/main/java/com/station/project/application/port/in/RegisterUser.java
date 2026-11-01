package com.station.project.application.port.in;


import com.station.project.domain.model.UserAuth;


public interface RegisterUser {
    public UserAuth register(UserAuth userRaw);
}

package com.station.project.application.port.out;

import java.util.Optional;

import com.station.project.domain.model.UserAuth;

//i don't like documentation of code but i must do D:
public interface UserAuthRepositoryPort {
    public UserAuth save(UserAuth user);
    public Optional<UserAuth> findByUserName(String userName);
}

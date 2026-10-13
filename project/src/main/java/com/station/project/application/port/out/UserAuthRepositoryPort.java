package com.station.project.application.port.out;

import java.util.UUID;

import com.station.project.domain.model.UserAuth;

public interface UserAuthRepositoryPort {
    UserAuth save(UserAuth user);

    UserAuth findByUserName(String userName);

    UserAuth findById(UUID id);

    void deleteById(UUID id);
}

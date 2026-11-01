package com.station.project.application.port.out;

import java.util.List;
import java.util.UUID;

import com.station.project.domain.model.User;

public interface UserRepositoryPort {
    User save(User user);

    User findById(UUID id);

    List<User> findAll();

    void deleteById(UUID id);
}

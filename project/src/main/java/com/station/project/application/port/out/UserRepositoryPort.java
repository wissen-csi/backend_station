package com.station.project.application.port.out;

import java.util.UUID;

import com.station.project.domain.model.User;

public interface UserRepositoryPort {
 User save(User user);
 public User findById(UUID id);

}

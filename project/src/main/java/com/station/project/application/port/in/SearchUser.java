package com.station.project.application.port.in;

import java.util.List;
import java.util.UUID;

import com.station.project.domain.model.User;

public interface SearchUser {
public User findById(UUID id);
public List<User> findAll();
}

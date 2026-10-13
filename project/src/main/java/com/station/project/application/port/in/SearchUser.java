package com.station.project.application.port.in;

import java.util.UUID;

import com.station.project.domain.model.User;

public interface SearchUser {
public User findById(UUID id);
}

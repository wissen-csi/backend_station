package com.station.project.infrastructure.dto;

import java.util.UUID;

import com.station.project.domain.enumerations.Role;

public record RegisterUserAuth(UUID id, String userName, String password, Role role, CreateUser user) {

}

package com.station.project.domain.model;

import java.util.UUID;

import javax.management.relation.Role;

import lombok.Builder;
@Builder 
public record UserAuth(UUID id, String userName, String password, Role role, User user) {
}
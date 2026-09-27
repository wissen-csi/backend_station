package com.station.project.domain.model;

import java.util.UUID;

import com.station.project.domain.enumerations.TokenType;

public record Token(UUID id, String token, boolean revoke, boolean expired, UserAuth user, TokenType tokenType) {
}
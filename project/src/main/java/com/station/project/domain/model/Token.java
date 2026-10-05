package com.station.project.domain.model;

import java.util.UUID;

import com.station.project.domain.enumerations.TokenType;

import lombok.Builder;
@Builder 
public record Token(UUID id, String token, boolean revoked, boolean expired, UserAuth user, TokenType tokenType) {
    public Token revokeToken() {
        return new Token(this.id, this.token, true, this.expired, this.user, this.tokenType);
    }

    public Token expire() {
        return new Token(this.id, this.token, this.revoked, true, this.user, this.tokenType);
    }

    public boolean isValid() {
        return !this.revoked && !this.expired;
    }
}
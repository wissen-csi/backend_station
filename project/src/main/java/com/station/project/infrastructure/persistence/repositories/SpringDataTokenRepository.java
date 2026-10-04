package com.station.project.infrastructure.persistence.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.station.project.infrastructure.persistence.Entities.TokenPersisted;

public interface SpringDataTokenRepository extends JpaRepository<TokenPersisted,UUID> {
    List<TokenPersisted> findAllByUserIdAndValidIsTrueAndRevokedIsFalse(UUID id);
}

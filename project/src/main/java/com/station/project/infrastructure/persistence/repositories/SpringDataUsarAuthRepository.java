package com.station.project.infrastructure.persistence.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.station.project.infrastructure.persistence.Entities.UserAuthPersisted;

public interface SpringDataUsarAuthRepository extends JpaRepository<UserAuthPersisted,UUID> {
    public Optional<UserAuthPersisted> findByUserName(String userName);
}

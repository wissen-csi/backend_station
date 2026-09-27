package com.station.project.infrastructure.persistence.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.station.project.infrastructure.persistence.Entities.UserPersisted;

public interface SpringDataUserRepository extends JpaRepository<UUID,UserPersisted> {
    Optional<UserPersisted> save(UserPersisted user);

}

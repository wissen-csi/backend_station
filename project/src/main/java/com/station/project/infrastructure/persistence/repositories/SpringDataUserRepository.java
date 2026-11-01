package com.station.project.infrastructure.persistence.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.station.project.infrastructure.persistence.Entities.UserPersisted;

public interface SpringDataUserRepository extends JpaRepository<UserPersisted,UUID> {

}

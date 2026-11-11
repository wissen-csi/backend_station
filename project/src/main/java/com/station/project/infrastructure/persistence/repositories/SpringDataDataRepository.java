package com.station.project.infrastructure.persistence.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.station.project.infrastructure.persistence.Entities.DataPersisted;

public interface SpringDataDataRepository extends JpaRepository<DataPersisted, UUID> {
    List<DataPersisted> findByFechaHoraBetweenOrderByFechaHoraAsc(OffsetDateTime desde, OffsetDateTime hasta);
}
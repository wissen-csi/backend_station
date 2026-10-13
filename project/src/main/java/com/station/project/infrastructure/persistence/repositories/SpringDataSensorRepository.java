package com.station.project.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.station.project.infrastructure.persistence.Entities.SensorPersisted;

public interface SpringDataSensorRepository extends JpaRepository<SensorPersisted, String> {

}


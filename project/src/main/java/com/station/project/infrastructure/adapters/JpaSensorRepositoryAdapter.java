package com.station.project.infrastructure.adapters;

import org.springframework.stereotype.Component;

import com.station.project.application.port.out.SensorRepositoryPort;
import com.station.project.domain.model.Sensor;
import com.station.project.infrastructure.persistence.repositories.SpringDataSensorRepository;
import com.station.project.infrastructure.utils.MapperEnt;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class JpaSensorRepositoryAdapter implements SensorRepositoryPort {
    private SpringDataSensorRepository repository;

    @Override
    public Sensor save(Sensor sensor) {
        return MapperEnt.sensor(repository.save(MapperEnt.sensorPersisted(sensor)));
    }

    @Override
    public Sensor findById(String id) {
        return MapperEnt.sensor(repository.findById(id).orElseThrow(() -> new EntityNotFoundException()));
    }
}


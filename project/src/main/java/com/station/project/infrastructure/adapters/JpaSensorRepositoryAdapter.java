package com.station.project.infrastructure.adapters;

import java.util.List;

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

    @Override
    public List<Sensor> findAll() {
        return repository.findAll().stream().map(MapperEnt::sensor).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}


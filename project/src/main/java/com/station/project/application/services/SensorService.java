package com.station.project.application.services;

import org.springframework.stereotype.Service;

import com.station.project.application.port.in.CreateSensor;
import com.station.project.application.port.in.DeleteSensor;
import com.station.project.application.port.in.SearchSensor;
import com.station.project.application.port.in.UpdateSensor;
import com.station.project.application.port.out.SensorRepositoryPort;
import com.station.project.domain.model.Sensor;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SensorService implements CreateSensor, UpdateSensor, DeleteSensor, SearchSensor {

    private SensorRepositoryPort repository;

    @Override
    public Sensor save(Sensor sensor) {
        return repository.save(sensor);
    }

    @Override
    public Sensor update(String id, String name) {
        Sensor sensor = repository.findById(id);
        return repository.save(Sensor.builder().id(sensor.id()).name(name).build());
    }

    @Override
    public boolean delete(String id) {
        Sensor sensor = repository.findById(id);
        repository.save(Sensor.builder().id(sensor.id()).name(sensor.name()).build());
        return true;
    }

    @Override
    public Sensor findById(String id) {
        return repository.findById(id);
    }
}


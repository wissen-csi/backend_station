package com.station.project.application.services;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.station.project.application.port.in.CreateData;
import com.station.project.application.port.in.DeleteData;
import com.station.project.application.port.in.SearchData;
import com.station.project.application.port.in.UpdateData;
import com.station.project.application.port.out.DataRepositoryPort;
import com.station.project.application.port.out.SensorRepositoryPort;
import com.station.project.domain.model.Data;
import com.station.project.domain.model.Sensor;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class DataService implements CreateData, UpdateData, DeleteData, SearchData {

    private DataRepositoryPort repository;
    private SensorRepositoryPort sensorRepository;

    @Override
    public Data save(Data data) {
        ensureSensorExists(data.sensor());
        return repository.save(data);
    }

    @Override
    public Data update(Data data, String id) {
        ensureSensorExists(data.sensor());
        Data existing = repository.findById(UUID.fromString(id));
        return repository.save(Data.builder()
                .id(existing.id())
                .sensor(data.sensor())
                .fechaHora(data.fechaHora())
                .temperatura(data.temperatura())
                .humedad(data.humedad())
                .presion(data.presion())
                .vientoVelocidad(data.vientoVelocidad())
                .vientoDireccion(data.vientoDireccion())
                .lluvia(data.lluvia())
                .calidad(data.calidad())
                .build());
    }

    @Override
    public boolean deleteData(String id) {
        repository.deleteById(UUID.fromString(id));
        return true;
    }

    @Override
    public Data findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Data> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Data> findByRange(OffsetDateTime desde, OffsetDateTime hasta) {
        return repository.findByRange(desde, hasta);
    }

    @Override
    public void saveAll(List<Data> list) {
        Set<String> sensorIds = new HashSet<>();
        for (Data data : list) {
            Sensor sensor = data.sensor();
            if (sensorIds.add(sensor.id())) {
                ensureSensorExists(sensor);
            }
        }
        repository.saveAll(list);
    }

    private void ensureSensorExists(Sensor sensor) {
        if (!sensorRepository.existsById(sensor.id())) {
            sensorRepository.save(sensor);
        }
    }
}
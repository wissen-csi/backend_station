package com.station.project.application.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.station.project.application.port.out.DataRepositoryPort;
import com.station.project.application.port.out.SensorRepositoryPort;
import com.station.project.domain.model.Data;
import com.station.project.domain.model.Sensor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataBatchPersistenceService {

    private final DataRepositoryPort dataRepository;
    private final SensorRepositoryPort sensorRepository;

    @Transactional
    public void persistBatch(List<Data> readings) {
        Set<String> sensorIds = new HashSet<>();
        for (Data reading : readings) {
            if (reading.id() == null) {
                throw new IllegalArgumentException("La lectura no tiene UUID y no se puede sincronizar");
            }

            Sensor sensor = reading.sensor();
            if (sensor == null || sensor.id() == null || sensor.id().isBlank() || sensor.name() == null) {
                throw new IllegalArgumentException(
                        "La lectura " + reading.id() + " no tiene un sensor válido");
            }

            if (sensorIds.add(sensor.id()) && !sensorRepository.existsById(sensor.id())) {
                sensorRepository.save(sensor);
            }
        }

        dataRepository.saveAll(readings);
    }
}

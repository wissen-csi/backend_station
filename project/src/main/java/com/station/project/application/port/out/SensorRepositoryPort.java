package com.station.project.application.port.out;

import com.station.project.domain.model.Sensor;

public interface SensorRepositoryPort {
    Sensor save(Sensor sensor);
    Sensor findById(String id);
}


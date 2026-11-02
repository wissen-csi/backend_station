package com.station.project.application.port.in;

import com.station.project.domain.model.Sensor;

public interface CreateSensor {
    Sensor save(Sensor sensor);
}


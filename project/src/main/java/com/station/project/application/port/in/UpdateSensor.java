package com.station.project.application.port.in;

import com.station.project.domain.model.Sensor;

public interface UpdateSensor {
    Sensor update(String id, String name);
}


package com.station.project.application.port.in;

import com.station.project.domain.model.Sensor;

public interface SearchSensor {
    Sensor findById(String id);
}


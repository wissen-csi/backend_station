package com.station.project.application.port.in;

import java.time.LocalDate;
import java.util.UUID;

import com.station.project.domain.model.Data;
import com.station.project.domain.model.Sensor;

public interface UpdateData {
    Data update(Sensor sensor, int data, int data1, int data2, LocalDate current, String id);
}


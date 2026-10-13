package com.station.project.application.port.in;

import com.station.project.domain.model.Data;

public interface CreateData {
    Data save(Data data);
}


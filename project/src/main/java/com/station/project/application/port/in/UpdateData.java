package com.station.project.application.port.in;

import com.station.project.domain.model.Data;

public interface UpdateData {
    Data update(Data data, String id);
}
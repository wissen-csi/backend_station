package com.station.project.application.port.in;

import java.util.List;

import com.station.project.domain.model.Data;

public interface CreateData {
    Data save(Data data);
    void saveAll(List<Data> list);
}


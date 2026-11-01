package com.station.project.application.port.in;

import java.util.List;
import java.util.UUID;

import com.station.project.domain.model.Data;

public interface FindDataCache {
    public Data findById(UUID id);
    public List<Data> findAll();
}

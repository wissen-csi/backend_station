package com.station.project.application.port.in;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.station.project.domain.model.Data;

public interface SearchData {
    Data findById(UUID id);
    List<Data> findAll();
    List<Data> findByRange(OffsetDateTime desde, OffsetDateTime hasta);
}
package com.station.project.application.port.out;

import java.util.List;
import java.util.UUID;

import com.station.project.domain.model.Data;

public interface DataRepositoryPort {
    Data save(Data data);
    Data findById(UUID id);
    void  saveAll(List<Data> list);
    List<Data> findAll();
    void deleteById(UUID id);
}


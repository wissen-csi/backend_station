package com.station.project.application.port.out;

import java.util.List;
import java.util.UUID;

import com.station.project.domain.model.Data;

public interface CacheDataRepositoryPort {
    public Data save(Data data);
    public Data findById(UUID id);
    public List<Data> findAll();
    public void deleteById(UUID id);
    public void deleteAll();
    public List<Data> saveAll(List<Data> list);
}

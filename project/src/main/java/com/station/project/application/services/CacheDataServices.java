package com.station.project.application.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.station.project.application.port.in.DeleteDataCache;
import com.station.project.application.port.in.FindDataCache;
import com.station.project.application.port.in.SaveDataCache;
import com.station.project.application.port.out.CacheDataRepositoryPort;
import com.station.project.domain.model.Data;

import lombok.AllArgsConstructor;

@AllArgsConstructor 
@Service 
public class CacheDataServices implements SaveDataCache, DeleteDataCache, FindDataCache {
    private CacheDataRepositoryPort cacheDataRepositoryPort;

    @Override
    public Data save(Data data) {
        return cacheDataRepositoryPort.save(data);
    }

    @Override
    public List<Data> saveAll(List<Data> list) {
        return cacheDataRepositoryPort.saveAll(list);
    }

    @Override
    public void deleteById(UUID id) {
        cacheDataRepositoryPort.deleteById(id);
    } 

    @Override
    public void deleteAll() {
        cacheDataRepositoryPort.deleteAll();
    }

    @Override
    public Data findById(UUID id) {
        return cacheDataRepositoryPort.findById(id);
    }

    @Override
    public List<Data> findAll() {
        return cacheDataRepositoryPort.findAll();
    }
}

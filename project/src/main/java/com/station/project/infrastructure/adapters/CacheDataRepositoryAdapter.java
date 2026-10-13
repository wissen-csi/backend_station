package com.station.project.infrastructure.adapters;

import java.util.List;
import java.util.UUID;

import com.station.project.application.port.out.CacheDataRepositoryPort;
import com.station.project.domain.model.Data;
import com.station.project.infrastructure.persistence.cache.repositories.SpringCacheDataRepository;

import lombok.AllArgsConstructor;
@AllArgsConstructor 
public class CacheDataRepositoryAdapter implements CacheDataRepositoryPort {
    private SpringCacheDataRepository springCacheDataRepository;
    @Override
    public Data save(Data data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public Data findById(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public List<Data> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public void deleteById(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public void saveAll(List<Data> list) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveAll'");
    }

}

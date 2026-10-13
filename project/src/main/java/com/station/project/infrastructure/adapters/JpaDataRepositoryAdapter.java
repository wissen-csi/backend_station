package com.station.project.infrastructure.adapters;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.station.project.application.port.out.DataRepositoryPort;
import com.station.project.domain.model.Data;
import com.station.project.infrastructure.persistence.repositories.SpringDataDataRepository;
import com.station.project.infrastructure.utils.MapperEnt;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class JpaDataRepositoryAdapter implements DataRepositoryPort {
    private SpringDataDataRepository repository;

    @Override
    public Data save(Data data) {
        return MapperEnt.data(repository.save(MapperEnt.dataPersisted(data)));
    }

    @Override
    public Data findById(UUID id) {
        return MapperEnt.data(repository.findById(id).orElseThrow(() -> new EntityNotFoundException()));
    }
}


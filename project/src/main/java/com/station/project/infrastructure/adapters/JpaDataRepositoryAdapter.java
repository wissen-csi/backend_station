package com.station.project.infrastructure.adapters;

import java.util.List;
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

    @Override
    public void saveAll(List<Data> list) {
        repository.saveAll(
            list.stream().map((x)->MapperEnt.dataPersisted(x)).toList()
        );
    }

    @Override
    public List<Data> findAll() {
        return repository.findAll().stream().map(MapperEnt::data).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}


package com.station.project.infrastructure.adapters;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import com.station.project.application.port.out.CacheDataRepositoryPort;
import com.station.project.domain.model.Data;
import com.station.project.infrastructure.persistence.cache.repositories.SpringCacheDataRepository;
import com.station.project.infrastructure.utils.MapperEnt;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
@AllArgsConstructor 
@Component 
public class CacheDataRepositoryAdapter implements CacheDataRepositoryPort {
    private SpringCacheDataRepository springCacheDataRepository;
    @Override
    public Data save(Data data) {
        return MapperEnt.dataD(springCacheDataRepository.save(MapperEnt.dataC(data)));
    }

    @Override
    public Data findById(UUID id) {
        return  MapperEnt.dataD(springCacheDataRepository.findById(id).orElseThrow(()-> new EntityNotFoundException()));
    }

    @Override
    public List<Data>  findAll() {    
       var iter = springCacheDataRepository.findAll();
       return  StreamSupport.stream(iter.spliterator(), false).map((x)->MapperEnt.dataD(x)).toList();
    }

    @Override
    public void deleteById(UUID id) {
        springCacheDataRepository.deleteById(id);
    }

    @Override
    public List<Data> saveAll(List<Data> list) {
       var iter=  springCacheDataRepository.saveAll(list.stream().map((x)-> MapperEnt.dataC(x)).toList());
        return StreamSupport.stream(iter.spliterator(), false).map((x)->MapperEnt.dataD(x)).toList();
    }

    @Override
    public void deleteAll() {
        springCacheDataRepository.deleteAll();;
    }
}

package com.station.project.application.services;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.station.project.application.port.in.CreateData;
import com.station.project.application.port.in.DeleteData;
import com.station.project.application.port.in.SearchData;
import com.station.project.application.port.in.UpdateData;
import com.station.project.application.port.out.DataRepositoryPort;
import com.station.project.domain.model.Data;
import com.station.project.domain.model.Sensor;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class DataService implements CreateData, UpdateData, DeleteData, SearchData {

    private DataRepositoryPort repository;

    @Override
    public Data save(Data data) {
        return repository.save(data);
    }

    @Override
    public Data update(Sensor sensor, int data, int data1, int data2, LocalDate current, String id) {
        Data existing = repository.findById(UUID.fromString(id));
        return repository.save(Data.builder()
                .id(existing.id())
                .sensor(sensor)
                .data(data)
                .data1(data1)
                .data2(data2)
                .current(current)
                .build());
    }

    @Override
    public boolean deleteData(String id) {
        Data existing = repository.findById(UUID.fromString(id));
        repository.save(Data.builder()
                .id(existing.id())
                .sensor(existing.sensor())
                .data(existing.data())
                .data1(existing.data1())
                .data2(existing.data2())
                .current(existing.current())
                .build());
        return true;
    }

    @Override
    public Data findById(UUID id) {
        return repository.findById(id);
    }
}


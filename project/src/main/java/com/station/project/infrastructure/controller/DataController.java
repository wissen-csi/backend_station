package com.station.project.infrastructure.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.station.project.application.services.DataService;
import com.station.project.application.services.SensorService;
import com.station.project.domain.model.Data;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;
    private final SensorService sensorService;

    @PostMapping
    public ResponseEntity<Data> create(@RequestBody DataRequest request) {
        Data created = dataService.save(toData(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<Data> findAll() {
        return dataService.findAll();
    }

    @GetMapping("/{id}")
    public Data findById(@PathVariable UUID id) {
        return dataService.findById(id);
    }

    @PutMapping("/{id}")
    public Data update(@PathVariable UUID id, @RequestBody DataRequest request) {
        Data data = toData(request);
        return dataService.update(
                data.sensor(),
                data.data(),
                data.data1(),
                data.data2(),
                data.current(),
                id.toString());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        dataService.deleteData(id.toString());
        return ResponseEntity.noContent().build();
    }

    private Data toData(DataRequest request) {
        return Data.builder()
                .sensor(sensorService.findById(request.sensorId()))
                .data(request.data())
                .data1(request.data1())
                .data2(request.data2())
                .current(request.current())
                .build();
    }

    public record DataRequest(
            String sensorId,
            int data,
            int data1,
            int data2,
            LocalDate current) {
    }
}

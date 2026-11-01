package com.station.project.infrastructure.controller;

import java.util.List;

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

import com.station.project.application.services.SensorService;
import com.station.project.domain.model.Sensor;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @PostMapping
    public ResponseEntity<Sensor> create(@RequestBody CreateSensorRequest request) {
        Sensor created = sensorService.save(Sensor.builder()
                .id(request.id())
                .name(request.name())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<Sensor> findAll() {
        return sensorService.findAll();
    }

    @GetMapping("/{id}")
    public Sensor findById(@PathVariable String id) {
        return sensorService.findById(id);
    }

    @PutMapping("/{id}")
    public Sensor update(@PathVariable String id, @RequestBody UpdateSensorRequest request) {
        return sensorService.update(id, request.name());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sensorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateSensorRequest(String id, String name) {
    }

    public record UpdateSensorRequest(String name) {
    }
}

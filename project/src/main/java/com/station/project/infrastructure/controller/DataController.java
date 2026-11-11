package com.station.project.infrastructure.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    public List<Data> find(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        if (desde != null && hasta != null) {
            return dataService.findByRange(desde, hasta);
        }
        return dataService.findAll();
    }

    @GetMapping("/{id}")
    public Data findById(@PathVariable UUID id) {
        return dataService.findById(id);
    }

    @PutMapping("/{id}")
    public Data update(@PathVariable UUID id, @RequestBody DataRequest request) {
        return dataService.update(toData(request), id.toString());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        dataService.deleteData(id.toString());
        return ResponseEntity.noContent().build();
    }

    private Data toData(DataRequest request) {
        return Data.builder()
                .sensor(sensorService.findById(request.sensorId()))
                .fechaHora(request.fechaHora())
                .temperatura(request.temperatura())
                .humedad(request.humedad())
                .presion(request.presion())
                .vientoVelocidad(request.vientoVelocidad())
                .vientoDireccion(request.vientoDireccion())
                .lluvia(request.lluvia())
                .calidad(request.calidad())
                .build();
    }

    public record DataRequest(
            String sensorId,
            @JsonProperty("fecha_hora") OffsetDateTime fechaHora,
            Double temperatura,
            Double humedad,
            Double presion,
            @JsonProperty("viento_velocidad") Double vientoVelocidad,
            @JsonProperty("viento_direccion") Double vientoDireccion,
            Double lluvia,
            String calidad) {
    }
}
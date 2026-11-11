package com.station.project.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record Data(
    UUID id,
    Sensor sensor,
    @JsonProperty("fecha_hora") OffsetDateTime fechaHora,
    Double temperatura,
    Double humedad,
    Double presion,
    @JsonProperty("viento_velocidad") Double vientoVelocidad,
    @JsonProperty("viento_direccion") Double vientoDireccion,
    Double lluvia,
    String calidad
) {}
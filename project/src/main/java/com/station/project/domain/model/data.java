package com.station.project.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record Data(
        UUID id,
        Sensor sensor,
        double windSpeed,
        Double windDirection,
        double precipitation,
        double temperature,
        double altitude,
        double humidity,
        double pressure,
        LocalDateTime measuredAt) {
}

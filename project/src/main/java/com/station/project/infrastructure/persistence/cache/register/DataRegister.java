package com.station.project.infrastructure.persistence.cache.register;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.annotation.Id;

import com.station.project.domain.model.Sensor;

import lombok.Builder;
import lombok.Data;

@RedisHash("Data")
@Data
@Builder
public class DataRegister implements Serializable {
    @Id
    private UUID id;
    private Sensor sensor;
    private double windSpeed;
    private Double windDirection;
    private double precipitation;
    private double temperature;
    private double altitude;
    private double humidity;
    private double pressure;
    private LocalDateTime measuredAt;
}

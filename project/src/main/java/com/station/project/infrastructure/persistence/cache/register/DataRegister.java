package com.station.project.infrastructure.persistence.cache.register;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.redis.core.RedisHash;

import com.station.project.domain.model.Sensor;

import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Data;

@RedisHash("Data")
@Data
@Builder
public class DataRegister implements Serializable {
    @Id
    private UUID id;
    private Sensor sensor;
    private OffsetDateTime fechaHora;
    private Double temperatura;
    private Double humedad;
    private Double presion;
    private Double vientoVelocidad;
    private Double vientoDireccion;
    private Double lluvia;
    private String calidad;
}
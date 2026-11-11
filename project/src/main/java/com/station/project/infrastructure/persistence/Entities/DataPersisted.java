package com.station.project.infrastructure.persistence.Entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "data")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DataPersisted {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    SensorPersisted sensor;

    @Column(name = "fecha_hora", nullable = false)
    OffsetDateTime fechaHora;

    Double temperatura;
    Double humedad;
    Double presion;

    @Column(name = "viento_velocidad")
    Double vientoVelocidad;

    @Column(name = "viento_direccion")
    Double vientoDireccion;

    Double lluvia;
    String calidad;
}
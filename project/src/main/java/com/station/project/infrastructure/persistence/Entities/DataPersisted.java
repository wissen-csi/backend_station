package com.station.project.infrastructure.persistence.Entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    UUID id;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    SensorPersisted sensor;

    @Column(nullable = false)
    double windSpeed;

    // Puede ser null mientras la veleta no esté funcionando.
    @Column
    Double windDirection;

    @Column(nullable = false)
    double precipitation;

    @Column(nullable = false)
    double temperature;

    @Column(nullable = false)
    double altitude;

    @Column(nullable = false)
    double humidity;

    @Column(nullable = false)
    double pressure;

    @Column(nullable = false)
    LocalDateTime measuredAt;
}

package com.station.project.infrastructure.persistence.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
@Entity 
@Table (name = "sensors")
@Builder 
public class sensorPersisted {
    @Id 
    private String id;
    @Column (nullable = false)
    private String name;
}
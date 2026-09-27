package com.station.project.infrastructure.persistence.Entities;

import java.time.LocalDate;
import java.util.UUID;

import com.station.project.domain.model.Sensor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
@Entity
@Table(name = "data")
@Builder 
public class DataPersisted {
    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    UUID id;
    @Column(nullable = false) 
    Sensor sensor; 
    @Column (nullable = false)
    int data;
    @Column (nullable = false)
    int data1;
    @Column (nullable = false)
    int data2;
    @Column (nullable = false)
    LocalDate current;
}

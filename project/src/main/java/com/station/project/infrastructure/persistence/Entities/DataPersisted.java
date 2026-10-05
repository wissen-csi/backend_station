package com.station.project.infrastructure.persistence.Entities;

import java.time.LocalDate;
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
    @GeneratedValue (strategy = GenerationType.UUID)
    UUID id;
    @ManyToOne
    @JoinColumn(name = "sensor_id")

    sensorPersisted sensor; 
    @Column (nullable = false)
    int data;
    @Column (nullable = false)
    int data1;
    @Column (nullable = false)
    int data2;
    @Column (nullable = false)
    LocalDate current;
}

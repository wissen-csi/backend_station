package com.station.project.infrastructure.persistence.cache.register;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.redis.core.RedisHash;

import com.station.project.domain.model.Sensor;

import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Data;
@RedisHash ("Data")
@Data 
@Builder 
public class DataRegister implements Serializable {
    @Id 
    private UUID id;
    private Sensor sensor;
    private int data;
    private int data1;
    private int data2;
    private LocalDate current;
}

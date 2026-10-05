package com.station.project.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public record Data(UUID id,Sensor sensor, int data , int data1, int data2, LocalDate current) {

}

package com.station.project.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public record data(UUID id,Sensor sensor, int data , int data1, int data2, LocalDate current) {

}

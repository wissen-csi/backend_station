package com.station.project.domain.model;

import lombok.Builder;

@Builder 
public record Sensor(String id, String name) {

}
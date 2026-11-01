package com.station.project.domain.model;

import java.io.Serializable;

import lombok.Builder;

@Builder 
public record Sensor(String id, String name) implements Serializable{

}
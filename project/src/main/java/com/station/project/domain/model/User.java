package com.station.project.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
@Builder 
public record User(UUID id, String name, String email, String dni, LocalDate birthdate, boolean active) {
}
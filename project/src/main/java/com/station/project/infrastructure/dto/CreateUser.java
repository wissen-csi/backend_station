package com.station.project.infrastructure.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CreateUser(UUID id, String name, String email, String dni, LocalDate birthdate) {

}

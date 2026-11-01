package com.station.project.infrastructure.dto;

import java.time.LocalDate;

public record RegisterRequestDTO(
        String userName,
        String password,
        String name,
        String email,
        String dni,
        LocalDate birthdate) {
}

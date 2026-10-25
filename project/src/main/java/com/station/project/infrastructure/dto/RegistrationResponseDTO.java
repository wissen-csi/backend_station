package com.station.project.infrastructure.dto;

import java.util.UUID;

public record RegistrationResponseDTO(UUID id, String userName, String role) {
}

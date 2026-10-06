package com.station.project.domain.model;

import java.time.LocalDate;

public record User(String name, String email, LocalDate birthdate) {

}

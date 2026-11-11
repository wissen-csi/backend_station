package com.station.project.application.port.in;

import java.time.LocalDate;

import com.station.project.domain.model.User;

public interface UpdateUser {
    public User update(String name, String email, String dni, LocalDate birthdate, String id);

}

package com.station.project.application.services;

import java.time.LocalDate;
import java.util.UUID;

import com.station.project.application.port.in.CreateUser;
import com.station.project.application.port.in.DeleteUser;
import com.station.project.application.port.in.UpdateUser;
import com.station.project.application.port.out.UserRepositoryPort;
import com.station.project.domain.model.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserService implements CreateUser, UpdateUser, DeleteUser {

    private UserRepositoryPort repository;

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public User update(String name, String email, String dni, LocalDate birthdate, String id) {
        User user = repository.findById(UUID.fromString(id));
        return repository
                .save(User
                    .builder()
                .id(user.id())
                .name(name)
                .email(email)
                .dni(dni)
                .birthdate(birthdate)
                .active(user.active())
                .build()
            );
    }

    @Override
    public boolean deleteUser(String id) {
        User user = repository.findById(UUID.fromString(id));
         repository
                .save(User
                    .builder()
                .id(user.id())
                .name(user.name())
                .email(user.email())
                .dni(user.dni())
                .birthdate(user.birthdate())
                .active(user.active())
                .build()
            );
        return true;
    }

}

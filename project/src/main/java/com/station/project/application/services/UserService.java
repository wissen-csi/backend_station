package com.station.project.application.services;

import com.station.project.application.port.in.CreateUser;
import com.station.project.application.port.out.UserRepositoryPort;
import com.station.project.domain.model.User;

import lombok.NoArgsConstructor;
@NoArgsConstructor 
public class UserService implements CreateUser {

    private  UserRepositoryPort repository;
    @Override
    public boolean create(User user) {
    return repository.save(user);
    }

}

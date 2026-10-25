package com.station.project.application.services;


import java.util.UUID;

import org.springframework.stereotype.Service;

import com.station.project.application.port.in.CreateAuthUser;
import com.station.project.application.port.in.DeleteUserAuth;
import com.station.project.application.port.in.FindByIdUserAuth;
import com.station.project.application.port.in.FindByNameUserAuth;
import com.station.project.application.port.in.UpdateUserAuth;
import com.station.project.application.port.out.PasswordEnconderPort;
import com.station.project.application.port.out.UserAuthRepositoryPort;
import com.station.project.domain.model.UserAuth;

import lombok.AllArgsConstructor;
@Service 
@AllArgsConstructor 
public class UserAuthService implements CreateAuthUser, FindByNameUserAuth, UpdateUserAuth, FindByIdUserAuth, DeleteUserAuth  {

    private UserAuthRepositoryPort repository;
    private PasswordEnconderPort passwordEnconderPort;

    @Override
    public UserAuth save(UserAuth userRaw) {
        
    System.out.println("PASSWORD RAW: " + userRaw.password());

    String encodedPassword = passwordEnconderPort.encode(userRaw.password());

    System.out.println("PASSWORD ENCODED: " + encodedPassword);

    UserAuth user = UserAuth.builder()
        .userName(userRaw.userName())
        .password(encodedPassword)
        .role(userRaw.role())
        .user(userRaw.user())
        .build();

    System.out.println("PASSWORD USER: " + user.password());

    return repository.save(user);

    }
    @Override
    public UserAuth findByUserName(String userName) {
        return repository.findByUserName(userName);
    }
    @Override
    public UserAuth updatePassword(String passwordRaw, UUID id) {
        UserAuth user = repository.findById(id);
        user = UserAuth.builder()
        .id(user.id())
        .user(user.user())
        .userName(user.userName())
        .password(passwordEnconderPort.encode(passwordRaw))
        .role(user.role())
        .build();
        return  repository.save(user);
    }
    @Override
    public UserAuth updateUserName(String userName, UUID id) {
        UserAuth user = repository.findById(id);
        user = UserAuth.builder()
        .id(user.id())
        .user(user.user())
        .userName(userName)
        .password(user.password())
        .role(user.role())
        .build();
        return  repository.save(user);
    }
    @Override
    public UserAuth findById(UUID id) {
        return  repository.findById(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        repository.deleteById(id);
        return true;
    }

}

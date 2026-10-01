package com.station.project.application.services;


import com.station.project.application.port.in.CreateAuthUser;
import com.station.project.application.port.in.findByNameUserAuth;
import com.station.project.application.port.out.PasswordEnconderPort;
import com.station.project.application.port.out.UserAuthRepositoryPort;
import com.station.project.domain.model.UserAuth;

import lombok.AllArgsConstructor;
@AllArgsConstructor 
public class UserAuthService implements CreateAuthUser, findByNameUserAuth  {

    private UserAuthRepositoryPort repository;
    private PasswordEnconderPort passwordEnconderPort;

    @Override
    public UserAuth save(UserAuth userRaw) {
        UserAuth user = UserAuth.builder()
        .userName(userRaw.userName())
        .password(passwordEnconderPort.encode(userRaw.password()))
        .role(userRaw.role())
        .user(userRaw.user())
        .build();
    return  repository.save(user);
    }
    @Override
    public UserAuth findByUserName(String userName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUserName'");
    }
}

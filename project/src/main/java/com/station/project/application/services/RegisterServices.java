package com.station.project.application.services;

import org.springframework.transaction.annotation.Transactional;

import com.station.project.application.port.in.RegisterUser;
import com.station.project.application.port.out.PasswordEnconderPort;
import com.station.project.domain.model.User;
import com.station.project.domain.model.UserAuth;

import lombok.AllArgsConstructor;
@AllArgsConstructor 
public class RegisterServices implements RegisterUser {
    private  UserAuthService userAuthService;
    private  UserService userService;
    private PasswordEnconderPort passwordEnconderPort;
    @Override
    @Transactional 
    public UserAuth register(UserAuth user) {
        User userData = userService.save(user.user());
        user = UserAuth.builder()
        .userName(user.userName())
        .password(passwordEnconderPort.encode(user.password()))
        .role(user.role())
        .user(userData)
        .build();
        return  userAuthService.save(user);
    }

}

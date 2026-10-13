package com.station.project.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.station.project.application.port.in.RegisterUser;
import com.station.project.domain.model.User;
import com.station.project.domain.model.UserAuth;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegisterServices implements RegisterUser {
    private UserAuthService userAuthService;
    private UserService userService;

    @Override
    @Transactional
    public UserAuth register(UserAuth user) {
        User userData = userService.save(user.user());
        UserAuth userAuth = UserAuth.builder()
                .userName(user.userName())
                .password(user.password())
                .role(user.role())
                .user(userData)
                .build();
        return userAuthService.save(userAuth);
    }
}

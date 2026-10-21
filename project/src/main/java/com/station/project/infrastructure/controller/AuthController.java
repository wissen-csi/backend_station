package com.station.project.infrastructure.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.util.Tuple;

import com.station.project.application.port.in.Login;
import com.station.project.application.port.in.Refresh;
import com.station.project.application.port.in.RegisterUser;
import com.station.project.domain.enumerations.Role;
import com.station.project.domain.model.User;
import com.station.project.domain.model.UserAuth;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUser registerUser;
    private final Login login;
    private final Refresh refresh;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegisterRequest request) {
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .dni(request.dni())
                .birthdate(request.birthdate())
                .active(true)
                .build();
        UserAuth created = registerUser.register(UserAuth.builder()
                .userName(request.userName())
                .password(request.password())
                .role(Role.USER)
                .user(user)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrationResponse(created.id(), created.userName(), created.role()));
    }

    @PostMapping("/login")
    public AuthTokenResponse login(@RequestBody LoginRequest request) {
        try {
            Tuple<String, String> tokens = login.login(request.userName(), request.password());
            return new AuthTokenResponse(tokens._1(), tokens._2());
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials", exception);
        }
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(
            @RequestHeader("Authorization") String authorization) {
            try {
                Tuple<String, String> token = refresh.refresh(authorization);
                return new RefreshTokenResponse(token._1(), token._2());
            } catch (IllegalArgumentException exception) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token", exception);
            }
    }

    public record RegisterRequest(
            String userName,
            String password,
            String name,
            String email,
            String dni,
            LocalDate birthdate) {
    }

    public record LoginRequest(String userName, String password) {
    }

    public record RegistrationResponse(java.util.UUID id, String userName, Role role) {
    }

    public record AuthTokenResponse(String accessToken, String refreshToken) {
    }

    public record RefreshTokenResponse(String userName, String accessToken) {
    }
}

package com.station.project.infrastructure.controller;

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
import com.station.project.infrastructure.dto.AuthTokenResponseDTO;
import com.station.project.infrastructure.dto.LoginRequestDTO;
import com.station.project.infrastructure.dto.RefreshTokenResponseDTO;
import com.station.project.infrastructure.dto.RegisterRequestDTO;
import com.station.project.infrastructure.dto.RegistrationResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUser registerUser;
    private final Login login;
    private final Refresh refresh;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> register(@RequestBody RegisterRequestDTO request) {
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
                .body(new RegistrationResponseDTO(created.id(), created.userName(), created.role().name()));
    }

    @PostMapping("/login")
    public AuthTokenResponseDTO login(@RequestBody LoginRequestDTO request) {
        try {
            Tuple<String, String> tokens = login.login(request.userName(), request.password());
            return new AuthTokenResponseDTO(tokens._1(), tokens._2());
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials", exception);
        }
    }

    @PostMapping("/refresh")
    public RefreshTokenResponseDTO refresh(
            @RequestHeader("Authorization") String authorization) {
            try {
                Tuple<String, String> token = refresh.refresh(authorization);
                return new RefreshTokenResponseDTO(token._1(), token._2());
            } catch (IllegalArgumentException exception) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token", exception);
            }
    }
}

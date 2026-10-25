package com.station.project.infrastructure.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.station.project.application.port.in.DeleteUser;
import com.station.project.application.port.in.SearchUser;
import com.station.project.application.port.in.UpdateUser;
import com.station.project.application.port.in.UpdateUserAuth;
import com.station.project.domain.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final SearchUser searchUser;
    private final UpdateUser updateUser;
    private final DeleteUser deleteUser;
    private final UpdateUserAuth updateUserAuth;

    @GetMapping
    public List<User> findAll() {
        return searchUser.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable UUID id) {
        return searchUser.findById(id);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        return updateUser.update(
                request.name(),
                request.email(),
                request.dni(),
                request.birthdate(),
                id.toString());
    }

    @PatchMapping("/{id}/username")
    public ResponseEntity<Void> updateUsername(
            @PathVariable UUID id,
            @RequestBody UpdateUsernameRequest request) {
        updateUserAuth.updateUserName(request.userName(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable UUID id,
            @RequestBody UpdatePasswordRequest request) {
        updateUserAuth.updatePassword(request.password(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteUser.deleteUser(id.toString());
        return ResponseEntity.noContent().build();
    }

    public record UpdateUserRequest(String name, String email, String dni, LocalDate birthdate) {
    }

    public record UpdateUsernameRequest(String userName) {
    }

    public record UpdatePasswordRequest(String password) {
    }
}

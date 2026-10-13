package com.station.project.application.port.in;

import java.util.UUID;

import com.station.project.domain.model.UserAuth;

public interface UpdateUserAuth {
public UserAuth updatePassword(String passwordRaw, UUID id);
public UserAuth updateUserName(String userName, UUID id);
}

package com.station.project.application.port.in;

import com.station.project.domain.model.UserAuth;

public interface CreateAuthUser {
 public UserAuth save(UserAuth user);
}

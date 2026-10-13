package com.station.project.application.port.in;

import java.util.UUID;

import com.station.project.domain.model.UserAuth;

public interface FindByIdUserAuth {
public UserAuth findById(UUID id);
}

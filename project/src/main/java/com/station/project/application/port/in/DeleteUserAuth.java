package com.station.project.application.port.in;

import java.util.UUID;

public interface DeleteUserAuth {
    boolean deleteById(UUID id);
}

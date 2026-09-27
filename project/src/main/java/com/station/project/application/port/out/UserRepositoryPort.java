package com.station.project.application.port.out;

import com.station.project.domain.model.User;

public interface UserRepositoryPort {
 boolean save(User user);

}

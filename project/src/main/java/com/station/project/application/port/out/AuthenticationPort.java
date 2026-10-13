package com.station.project.application.port.out;

public interface AuthenticationPort {
    void auth(String userName, String password);
}

package com.station.project.application.port.out;

public interface PasswordEnconderPort {
String encode(String rawPassword);
boolean matches(String rawPassword, String encodedPassword);
}

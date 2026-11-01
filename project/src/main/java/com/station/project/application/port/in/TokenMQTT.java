package com.station.project.application.port.in;

import com.station.project.domain.model.UserAuth;

public interface TokenMQTT {
    String tokenFront(UserAuth user);
    String tokenAdmin(UserAuth userAuth);
    String tokenBoya(UserAuth userAuth);
    String systemToken();
    boolean isTokenActive(String token);
    boolean isAuthorized(String token, String topic, int access, String configuredTopic);

}

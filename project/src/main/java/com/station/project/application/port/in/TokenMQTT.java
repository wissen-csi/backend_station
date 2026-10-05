package com.station.project.application.port.in;

import com.station.project.domain.model.UserAuth;

public interface TokenMQTT {
public String tokenFront(UserAuth user);
public String tokenAdmin(UserAuth userAuth);
public String tokenBoya(UserAuth userAuth);

}

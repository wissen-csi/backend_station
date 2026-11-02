package com.station.project.application.port.in;

import com.station.project.domain.model.Token;

public interface CreateToken {
    public Token save(Token token);

}

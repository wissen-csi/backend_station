package com.station.project.infrastructure.adapters;

import java.util.List;

import com.station.project.application.port.out.TokenRepositoryPort;
import com.station.project.domain.model.Token;
import com.station.project.domain.model.UserAuth;
import com.station.project.infrastructure.persistence.repositories.SpringDataTokenRepository;
import com.station.project.infrastructure.utils.MapperEnt;

import lombok.AllArgsConstructor;
@AllArgsConstructor 
public class JpaTokenRepositoryAdapter implements TokenRepositoryPort{

        private SpringDataTokenRepository repository;
    @Override
    public Token save(Token token) {
        return MapperEnt.token(repository.save(MapperEnt.tokenPersisted(token)));
    }
    @Override
    public List<Token> findAllValidIsFalseOrRevokedIsFalseByUserId(UserAuth user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllValidIsFalseOrRevokedIsFalseByUserId'");
    }

}

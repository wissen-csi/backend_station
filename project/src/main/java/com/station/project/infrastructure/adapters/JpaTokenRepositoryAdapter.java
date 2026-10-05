package com.station.project.infrastructure.adapters;

import java.util.List;

import com.station.project.application.port.out.TokenRepositoryPort;
import com.station.project.domain.model.Token;
import com.station.project.domain.model.UserAuth;
import com.station.project.infrastructure.persistence.Entities.TokenPersisted;
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
    public List<Token> findAllByUserAuthIdAndExpiredIsFalseAndRevokedIsFalse(UserAuth user) {
        List<TokenPersisted> list = repository.findAllByUserAuthIdAndExpiredIsFalseAndRevokedIsFalse(user.id());
        return list.stream().map((token) -> MapperEnt.token(token)).toList();
    }
    @Override
    public List<Token> findAll() {
        
        return  repository.findAll().stream().map((token) -> MapperEnt.token(token)).toList();
    }
    @Override
    public List<Token> saveAll(List<Token> list) {
         
        return repository.saveAll(list.stream().map((token)-> MapperEnt.tokenPersisted(token)).toList()).stream().map((token) -> MapperEnt.token(token)).toList();

    }
    

}

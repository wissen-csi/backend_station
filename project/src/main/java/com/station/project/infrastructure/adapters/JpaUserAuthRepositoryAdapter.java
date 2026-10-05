package com.station.project.infrastructure.adapters;


import java.util.Optional;

import com.station.project.application.port.out.UserAuthRepositoryPort;
import com.station.project.domain.model.UserAuth;
import com.station.project.infrastructure.persistence.Entities.UserAuthPersisted;
import com.station.project.infrastructure.persistence.repositories.SpringDataUsarAuthRepository;
import com.station.project.infrastructure.utils.MapperEnt;

import lombok.AllArgsConstructor;
 
@AllArgsConstructor 
public class JpaUserAuthRepositoryAdapter implements UserAuthRepositoryPort {

    private SpringDataUsarAuthRepository repository;



    @Override
    public Optional<UserAuth> findByUserName(String userName) {
        Optional<UserAuthPersisted> opt = repository.findByUserName(userName);
        if (opt.isEmpty()) {
            return Optional.empty();
        }else{
            return Optional.of(MapperEnt.userAuth(opt.get()));
        }
    }



    @Override
    public UserAuth save(UserAuth user) {
        return MapperEnt.userAuth(repository.save(MapperEnt.userAuthPersisted(user)));
    }


}

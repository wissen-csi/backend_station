package com.station.project.infrastructure.adapters;
import java.util.Optional;

import  com.station.project.application.port.out.UserRepositoryPort;
import com.station.project.domain.model.User;
import com.station.project.infrastructure.persistence.Entities.UserPersisted;
import com.station.project.infrastructure.persistence.repositories.SpringDataUserRepository;
import com.station.project.infrastructure.utils.Mapper;

import lombok.NoArgsConstructor;
@NoArgsConstructor 
public class JpaUserRepositoryAdapter implements UserRepositoryPort {
    private  SpringDataUserRepository repository;

    @Override
    public boolean save(User user) {
        Optional<UserPersisted> opt = repository.save(Mapper.UserPersisted(user));
        if(opt.isEmpty())
            return false;
        return true;
    }

    
}

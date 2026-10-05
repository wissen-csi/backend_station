package com.station.project.infrastructure.adapters;

import org.springframework.stereotype.Component;

import  com.station.project.application.port.out.UserRepositoryPort;
import com.station.project.domain.model.User;
import com.station.project.infrastructure.persistence.Entities.UserPersisted;
import com.station.project.infrastructure.persistence.repositories.SpringDataUserRepository;
import com.station.project.infrastructure.utils.MapperEnt;

import lombok.AllArgsConstructor;
@AllArgsConstructor 
@Component 
public class JpaUserRepositoryAdapter implements UserRepositoryPort {
    private  SpringDataUserRepository repository;

    @Override
    public User save(User user) {
        return MapperEnt.user(repository.save(new UserPersisted()));
        
    }

    
}

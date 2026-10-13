package com.station.project.infrastructure.adapters;



import java.util.UUID;

import org.springframework.stereotype.Component;

import com.station.project.application.port.out.UserAuthRepositoryPort;
import com.station.project.domain.model.UserAuth;
import com.station.project.infrastructure.persistence.repositories.SpringDataUsarAuthRepository;
import com.station.project.infrastructure.utils.MapperEnt;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
 @Component 
@AllArgsConstructor 
public class JpaUserAuthRepositoryAdapter implements UserAuthRepositoryPort {

    private SpringDataUsarAuthRepository repository;



    @Override
    public UserAuth findByUserName(String userName) {
        return MapperEnt.userAuth(repository.findByUserName(userName).orElseThrow(() -> new EntityNotFoundException(userName)));
    }



    @Override
    public UserAuth save(UserAuth user) {
        return MapperEnt.userAuth(repository.save(MapperEnt.userAuthPersisted(user)));
    }



    @Override
    public UserAuth findById(UUID id) {
        return MapperEnt.userAuth(repository.findById(id).orElseThrow(()-> new EntityNotFoundException()));
    }



    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }


}

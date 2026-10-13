package com.station.project.infrastructure.persistence.cache.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.station.project.infrastructure.persistence.cache.register.DataRegister;

public interface SpringCacheDataRepository extends CrudRepository<DataRegister,UUID> {

}

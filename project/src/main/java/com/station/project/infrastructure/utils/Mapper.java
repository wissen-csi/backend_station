package com.station.project.infrastructure.utils;

import com.station.project.domain.model.User;
import com.station.project.infrastructure.persistence.Entities.UserPersisted;

public class Mapper {
 public static UserPersisted UserPersisted(User user){
    return UserPersisted.builder()
    .name(user.name())
    .email(user.email())
    .dni(user.dni())
    .birthdate(user.birthdate())
    .build();
 }
}

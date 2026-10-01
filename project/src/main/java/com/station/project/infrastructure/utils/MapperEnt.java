package com.station.project.infrastructure.utils;

import com.station.project.domain.model.User;
import com.station.project.domain.model.UserAuth;
import com.station.project.infrastructure.persistence.Entities.UserAuthPersisted;
import com.station.project.infrastructure.persistence.Entities.UserPersisted;

public class MapperEnt {
 public static UserPersisted UserPersisted(User user){
    return UserPersisted.builder()
    .name(user.name())
    .email(user.email())
    .dni(user.dni())
    .birthdate(user.birthdate())
    .build();
 }
 public static UserAuthPersisted userAuthPersisted(UserAuth user){
   return  UserAuthPersisted.builder()
   .userName(user.userName())
   .role(user.role())
   .user(user.user())
   .build();
 }
 public  static  UserAuth userAuth(UserAuthPersisted user){
  return  UserAuth.builder()
  .id(user.getId())
  .userName(user.getUserName())
  .password(user.getPassword())
  .role(user.getRole())
  .user(user.getUser())
  .build();
 }
 public static User user(UserPersisted user){
      return  User.builder()
      .id(user.getId())
      .name(user.getName())
      .email(user.getEmail())
      .dni(user.getDni())
      .birthdate(user.getBirthdate())
      .build();
 }
}

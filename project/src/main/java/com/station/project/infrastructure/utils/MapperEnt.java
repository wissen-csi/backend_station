package com.station.project.infrastructure.utils;

import com.station.project.domain.model.Data;
import com.station.project.domain.model.Sensor;
import com.station.project.domain.model.Token;
import com.station.project.domain.model.User;
import com.station.project.domain.model.UserAuth;
import com.station.project.infrastructure.persistence.Entities.DataPersisted;
import com.station.project.infrastructure.persistence.Entities.SensorPersisted;
import com.station.project.infrastructure.persistence.Entities.TokenPersisted;
import com.station.project.infrastructure.persistence.Entities.UserAuthPersisted;
import com.station.project.infrastructure.persistence.Entities.UserPersisted;
import com.station.project.infrastructure.persistence.cache.register.DataRegister;

public class MapperEnt {
    public static UserPersisted UserPersisted(User user) {
        return UserPersisted.builder()
                .id(user.id())
                .name(user.name())
                .email(user.email())
                .dni(user.dni())
                .birthdate(user.birthdate())
                .active(user.active())
                .build();
    }

    public static UserAuthPersisted userAuthPersisted(UserAuth user) {
        return UserAuthPersisted.builder()
                .id(user.id())
                .password(user.password())
                .userName(user.userName())
                .role(user.role())
                .user(UserPersisted(user.user()))
                .build();
    }

    public static UserAuth userAuth(UserAuthPersisted user) {
        return UserAuth.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .password(user.getPassword())
                .role(user.getRole())
                .user(user(user.getUser()))
                .build();
    }

    public static User user(UserPersisted user) {
        return User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .dni(user.getDni())
                .birthdate(user.getBirthdate())
                .active(user.isActive())
                .build();
    }

    public static TokenPersisted tokenPersisted(Token token) {
        return TokenPersisted.builder()
                .id(token.id())
                .token(token.token())
                .revoked(token.revoked())
                .expired(token.expired())
                .userAuth(userAuthPersisted(token.user()))
                .tokenType(token.tokenType())
                .build();
    }

    public static Token token(TokenPersisted token) {
        return Token.builder()
                .id(token.getId())
                .token(token.getToken())
                .revoked(token.isRevoked())
                .expired(token.isExpired())
                .user(userAuth(token.getUserAuth()))
                .tokenType(token.getTokenType())
                .build();
    }

    public static Data data(DataRegister data) {
        return Data.builder()
                .id(data.getId())
                .sensor(data.getSensor())
                .data(data.getData())
                .data1(data.getData1())
                .data2(data.getData2())
                .current(data.getCurrent())
                .build();
    }

    public static SensorPersisted sensorPersisted(Sensor sensor) {
        return SensorPersisted.builder()
                .id(sensor.id())
                .name(sensor.name())
                .build();
    }

    public static Sensor sensor(SensorPersisted sensor) {
        return Sensor.builder()
                .id(sensor.getId())
                .name(sensor.getName())
                .build();
    }

    public static DataPersisted dataPersisted(Data data) {
        return DataPersisted.builder()
                .id(data.id())
                .sensor(sensorPersisted(data.sensor()))
                .data(data.data())
                .data1(data.data1())
                .data2(data.data2())
                .current(data.current())
                .build();
    }

    public static Data data(DataPersisted data) {
        return Data.builder()
                .id(data.getId())
                .sensor(sensor(data.getSensor()))
                .data(data.getData())
                .data1(data.getData1())
                .data2(data.getData2())
                .current(data.getCurrent())
                .build();
    }
    public static Data dataD(DataRegister data){
        return Data.builder().id(data.getId()).sensor(data.getSensor()).data(data.getData()).data1(data.getData1()).data2(data.getData2()).current(data.getCurrent()).build();
    }
    public static DataRegister dataC(Data data){
        return DataRegister.builder().id(data.id()).sensor(data.sensor()).data(data.data()).data1(data.data1()).data2(data.data2()).current(data.current()).build();
    }
}


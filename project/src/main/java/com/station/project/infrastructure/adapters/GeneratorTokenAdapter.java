package com.station.project.infrastructure.adapters;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;

import com.station.project.application.port.out.GeneratorTokenPort;
import com.station.project.domain.model.UserAuth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class GeneratorTokenAdapter implements GeneratorTokenPort {
    @Value ("${jwt.secret-key}")
    private  String keyRaw;
    @Value ("${jwt.expiration}")
    private long expiration;
    @Value ("${jwt.refresh}")
    private long refresh;
    @Override
    public String generate(String id, UserAuth user, long expiration) {
        return Jwts.builder()
        .id(user.id().toString())
        .claims(Map.of("userName",user.userName(),"role",user.role()))
        .subject(user.userName())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis()+ expiration))
        .signWith(key())
        .compact();

    }
    private SecretKey key(){
        byte[] key = Decoders.BASE64.decode(keyRaw);
        return Keys.hmacShaKeyFor(key);
    }

}

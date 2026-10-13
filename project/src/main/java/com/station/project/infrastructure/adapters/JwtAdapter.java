package com.station.project.infrastructure.adapters;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.station.project.application.port.out.JwtPort;
import com.station.project.domain.model.UserAuth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Component 
public class JwtAdapter implements JwtPort {
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
        .claims(Map.of("userName",user.userName(),"role",user.role(),"active",user.user().active()))
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
    @Override
    public String generateToken(UserAuth user) {
        return generate(user.id().toString(), user, this.expiration);
    }
    @Override
    public String generateRefreshToken(UserAuth user) {
        return generate(user.id().toString(), user, this.refresh);
    }
    @Override
    public String generateMQTTFront() {
        return Jwts.builder()
    .id(UUID.randomUUID().toString())
    .subject("frontend")
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis()+expiration))
    .signWith(key())
    .compact();
    }
    @Override
    public String generateMQTTAdmin() {
                return Jwts.builder()
    .id(UUID.randomUUID().toString())
    .subject("admin")
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis()+expiration))
    .signWith(key())
    .compact();
    }
    @Override
    public String generateMQTTBoya() {
        return Jwts.builder()
    .id(UUID.randomUUID().toString())
    .subject("boya-001")
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis()+expiration))
    .signWith(key())
    .compact();
    }
    @Override
    public String extractUserName(String token) {
        Claims  jwt = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        return jwt.getSubject();
    }
    @Override
    public boolean isTokenValid(String token, UserAuth userAuth) {
        String userName = extractUserName(token);
        String name = userAuth.userName();
        return userName.equals(name);
    }
    @Override
    public boolean isTokenExpired(String token) {
        return extractExpired(token).before(new Date());
        
    }
    private Date extractExpired(String token){
        Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        return claims.getExpiration();
    }


    

}

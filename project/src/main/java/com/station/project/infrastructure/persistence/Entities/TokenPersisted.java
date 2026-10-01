package com.station.project.infrastructure.persistence.Entities;

import java.util.UUID;

import com.station.project.domain.enumerations.TokenType;
import com.station.project.domain.model.UserAuth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "token")
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
@Getter 
@Setter 
public class TokenPersisted {
    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;
    @Column (nullable = false)
    private String token;
    @Column (nullable = false)
    private boolean revoke;
    @Column (nullable = false)
    private  boolean expired;
    @OneToOne (fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn (name = "user_id", nullable = false)
    private UserAuth userAuth;
    @Column (nullable = false)
    @Builder.Default
    private TokenType tokenType = TokenType.BARRER;

}

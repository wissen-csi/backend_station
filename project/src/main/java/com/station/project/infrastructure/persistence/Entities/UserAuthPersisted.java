package com.station.project.infrastructure.persistence.Entities;

import java.util.UUID;

import com.station.project.domain.enumerations.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "users_auth")
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
@Getter 
@Setter 
public class UserAuthPersisted {
    @Id 
    private UUID id;
    @Column(nullable = false,name = "user_name", unique = true)
    private String userName;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne (fetch = FetchType.LAZY)
    @MapsId 
    @JoinColumn (name = "user_id", nullable = false, unique = true)
    private  UserPersisted user;
}

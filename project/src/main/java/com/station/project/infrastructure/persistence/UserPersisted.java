package com.station.project.infrastructure.persistence;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity 
public class UserPersisted {
    @Column 
    private String name;
    @Column 
    private String email;
    @Column 
    private LocalDate birthdate;

}

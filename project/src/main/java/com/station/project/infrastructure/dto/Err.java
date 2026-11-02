package com.station.project.infrastructure.dto;

import org.springframework.http.HttpStatusCode;

/**
 * Err
 */
public record Err(int num, String message, String status) {
    public Err(HttpStatusCode code, Exception exception){
        var type = Exception.class;
        this(code.value(), type.getSimpleName() , code.toString());
    }
}
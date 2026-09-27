package com.station.project.infrastructure.handler_err;

import org.hibernate.NonUniqueResultException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.station.project.infrastructure.dto.Err;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

@RestControllerAdvice 
public class ErrHandlerPersistence {
    @ExceptionHandler (EntityNotFoundException.class)
    public  ResponseEntity<Err> notFound(EntityNotFoundException exception){
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(new Err(HttpStatusCode.valueOf(400), exception));
    }
    @ExceptionHandler (NonUniqueResultException.class)
    public  ResponseEntity<Err> nonUnique(NonUniqueResultException exception){
        return  ResponseEntity.status(HttpStatusCode.valueOf(409)).body(new Err(HttpStatusCode.valueOf(409), exception));
    }
    public ResponseEntity<Err> noResult(NoResultException exception){
        return  ResponseEntity.status(HttpStatusCode.valueOf(500)).body(new Err(HttpStatusCode.valueOf(500), exception));

    }
}

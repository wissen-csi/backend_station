package com.station.project.infrastructure.handler_err;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.station.project.infrastructure.dto.Err;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ErrHandlerGeneric {

    private static final Logger LOG = LoggerFactory.getLogger(ErrHandlerGeneric.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Err> generic(Exception exception) {
        LOG.warn("generic", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Err(500, "err", HttpStatus.INTERNAL_SERVER_ERROR.name()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Err> userNotFound(UsernameNotFoundException usernameNotFoundException) {
        LOG.warn("User not found", usernameNotFoundException);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Err(404, "User not found", HttpStatus.NOT_FOUND.name()));
    }

@ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Err> genmeicfound(EntityNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Err(404, "not found", HttpStatus.NOT_FOUND.name()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Err>dbIntegrity(DataIntegrityViolationException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new Err(409,exception.getMessage(),HttpStatus.CONFLICT.name()));
    }

}
package com.griddynamics.ngolovin.store.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super("User with email [" + email + "] already exists");
    }
}

package com.griddynamics.ngolovin.store.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("User with email [" + email + "] doesn't exist");
    }
}

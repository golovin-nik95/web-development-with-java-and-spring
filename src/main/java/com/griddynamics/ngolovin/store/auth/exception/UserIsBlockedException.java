package com.griddynamics.ngolovin.store.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserIsBlockedException extends RuntimeException {

    public UserIsBlockedException() {
        super("Exceeded login attempts for this ip address");
    }
}

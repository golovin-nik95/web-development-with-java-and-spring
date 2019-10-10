package com.griddynamics.ngolovin.store.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientProductQuantityException extends RuntimeException {

    public InsufficientProductQuantityException(Long id) {
        super("Insufficient quantity of product with id [" + id + "]");
    }
}

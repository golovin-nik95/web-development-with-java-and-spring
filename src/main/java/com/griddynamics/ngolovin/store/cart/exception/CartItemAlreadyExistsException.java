package com.griddynamics.ngolovin.store.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CartItemAlreadyExistsException extends RuntimeException {

    public CartItemAlreadyExistsException(Long productId) {
        super("Item with productId [" + productId + "] already exists");
    }
}

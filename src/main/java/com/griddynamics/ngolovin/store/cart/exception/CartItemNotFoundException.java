package com.griddynamics.ngolovin.store.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(Long productId) {
        super("Item with productId [" + productId + "] doesn't exists");
    }
}

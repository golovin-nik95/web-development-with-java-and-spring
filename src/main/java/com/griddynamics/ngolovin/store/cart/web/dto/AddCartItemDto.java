package com.griddynamics.ngolovin.store.cart.web.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class AddCartItemDto {

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Long quantity;
}

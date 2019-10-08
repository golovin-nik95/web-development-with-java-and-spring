package com.griddynamics.ngolovin.store.cart.web.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UpdateCartItemDto {

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Long quantity;
}

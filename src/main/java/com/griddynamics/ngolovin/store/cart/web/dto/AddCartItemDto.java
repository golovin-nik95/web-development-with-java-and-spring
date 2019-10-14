package com.griddynamics.ngolovin.store.cart.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemDto {

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Long quantity;
}

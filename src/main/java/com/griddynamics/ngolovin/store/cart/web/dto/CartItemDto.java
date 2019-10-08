package com.griddynamics.ngolovin.store.cart.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private int ordinal;
    private String productTitle;
    private long quantity;
    private BigDecimal subtotal;
}

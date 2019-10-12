package com.griddynamics.ngolovin.store.order.web.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {

    private int ordinal;
    private String productTitle;
    private long quantity;
    private BigDecimal subtotal;
}

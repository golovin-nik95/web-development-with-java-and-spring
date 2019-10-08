package com.griddynamics.ngolovin.store.product.web.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {

    private Long id;
    private String title;
    private Long available;
    private BigDecimal price;
}

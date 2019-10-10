package com.griddynamics.ngolovin.store.cart.domain;

import com.griddynamics.ngolovin.store.product.domain.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private ProductEntity product;
    private long quantity;
}

package com.griddynamics.ngolovin.store.cart.domain;

import com.griddynamics.ngolovin.store.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private Product product;
    private long quantity;
}

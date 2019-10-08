package com.griddynamics.ngolovin.store.cart.service;

import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.exception.CartItemAlreadyExistsException;
import com.griddynamics.ngolovin.store.cart.exception.CartItemNotFoundException;
import com.griddynamics.ngolovin.store.cart.exception.ProductNotFoundException;
import com.griddynamics.ngolovin.store.product.domain.Product;
import com.griddynamics.ngolovin.store.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductService productService;
    private final Cart cart;

    public Cart getCart() {
        return cart;
    }

    //TODO(ngolovin) exception handling
    public CartItem addItem(Long productId, Long quantity) {
        Product product = productService.getById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (quantity > product.getAvailable()) {
            throw new RuntimeException();
        }

        CartItem existedItem = cart.getItems().get(productId);
        if (existedItem != null) {
            throw new CartItemAlreadyExistsException(productId);
        }

        return putItem(productId, quantity, product);
    }

    //TODO(ngolovin) exception handling
    public CartItem updateItem(Long productId, Long quantity) {
        Product product = productService.getById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (quantity > product.getAvailable()) {
            throw new RuntimeException();
        }

        CartItem existedItem = cart.getItems().get(productId);
        if (existedItem == null) {
            throw new CartItemNotFoundException(productId);
        }

        return putItem(productId, quantity, product);
    }

    public void removeItem(Long productId) {
        CartItem item = cart.getItems().remove(productId);
        if (item == null) {
            throw new CartItemNotFoundException(productId);
        }
    }

    private CartItem putItem(Long productId, Long quantity, Product product) {
        CartItem item = new CartItem(product, quantity);
        cart.getItems().put(productId, item);
        return item;
    }
}

package com.griddynamics.ngolovin.store.cart.service;

import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.exception.CartItemAlreadyExistsException;
import com.griddynamics.ngolovin.store.cart.exception.CartItemNotFoundException;
import com.griddynamics.ngolovin.store.cart.exception.InsufficientProductQuantityException;
import com.griddynamics.ngolovin.store.cart.exception.ProductNotFoundException;
import com.griddynamics.ngolovin.store.cart.web.dto.AddCartItemDto;
import com.griddynamics.ngolovin.store.cart.web.dto.UpdateCartItemDto;
import com.griddynamics.ngolovin.store.product.domain.ProductEntity;
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

    public CartItem addItem(AddCartItemDto addCartItemDto) {
        Long productId = addCartItemDto.getProductId();
        CartItem existedItem = cart.getItems().get(productId);
        if (existedItem != null) {
            throw new CartItemAlreadyExistsException(productId);
        }

        Long quantity = addCartItemDto.getQuantity();
        ProductEntity product = getProduct(productId, quantity);

        return putItem(productId, quantity, product);
    }

    public CartItem updateItem(UpdateCartItemDto updateCartItemDto) {
        Long productId = updateCartItemDto.getProductId();
        CartItem existedItem = cart.getItems().get(productId);
        if (existedItem == null) {
            throw new CartItemNotFoundException(productId);
        }

        Long quantity = updateCartItemDto.getQuantity();
        ProductEntity product = getProduct(productId, quantity);

        return putItem(productId, quantity, product);
    }

    public void removeItem(Long productId) {
        CartItem item = cart.getItems().remove(productId);
        if (item == null) {
            throw new CartItemNotFoundException(productId);
        }
    }

    private ProductEntity getProduct(Long productId, Long quantity) {
        ProductEntity product = productService.getById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (quantity > product.getAvailable()) {
            throw new InsufficientProductQuantityException(productId);
        }

        return product;
    }

    private CartItem putItem(Long productId, Long quantity, ProductEntity product) {
        CartItem item = new CartItem(product, quantity);
        cart.getItems().put(productId, item);

        return item;
    }
}

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

    private final Cart cart;
    private final ProductService productService;

    public Cart getCart() {
        return cart;
    }

    public CartItem addCartItem(AddCartItemDto addCartItemDto) {
        Long productId = addCartItemDto.getProductId();
        CartItem existedCartItem = cart.getItems().get(productId);
        if (existedCartItem != null) {
            throw new CartItemAlreadyExistsException(productId);
        }

        Long quantity = addCartItemDto.getQuantity();
        ProductEntity product = getProduct(productId, quantity);

        return putCartItem(productId, quantity, product);
    }

    public CartItem updateCartItem(UpdateCartItemDto updateCartItemDto) {
        Long productId = updateCartItemDto.getProductId();
        CartItem existedCartItem = cart.getItems().get(productId);
        if (existedCartItem == null) {
            throw new CartItemNotFoundException(productId);
        }

        Long quantity = updateCartItemDto.getQuantity();
        ProductEntity product = getProduct(productId, quantity);

        return putCartItem(productId, quantity, product);
    }

    public void removeCartItem(Long productId) {
        CartItem cartItem = cart.getItems().remove(productId);
        if (cartItem == null) {
            throw new CartItemNotFoundException(productId);
        }
    }

    private ProductEntity getProduct(Long productId, Long quantity) {
        ProductEntity product = productService.getProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (quantity > product.getAvailable()) {
            throw new InsufficientProductQuantityException(productId);
        }

        return product;
    }

    private CartItem putCartItem(Long productId, Long quantity, ProductEntity product) {
        CartItem cartItem = new CartItem(product, quantity);
        cart.getItems().put(productId, cartItem);

        return cartItem;
    }
}

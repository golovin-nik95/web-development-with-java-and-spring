package com.griddynamics.ngolovin.store.cart.web.controller;

import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.service.CartService;
import com.griddynamics.ngolovin.store.cart.web.dto.AddCartItemDto;
import com.griddynamics.ngolovin.store.cart.web.dto.CartDto;
import com.griddynamics.ngolovin.store.cart.web.dto.CartItemDto;
import com.griddynamics.ngolovin.store.cart.web.dto.UpdateCartItemDto;
import com.griddynamics.ngolovin.store.cart.web.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartDto getCart() {
        Cart cart = cartService.getCart();

        return CartMapper.INSTANCE.convert(cart);
    }

    @PostMapping("item")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemDto addItem(@RequestBody @Valid AddCartItemDto addCartItemDto) {
        CartItem item = cartService.addItem(addCartItemDto);
        Cart cart = cartService.getCart();

        return CartMapper.INSTANCE.convert(item, cart);
    }

    @PutMapping("item")
    public CartItemDto updateItem(@RequestBody @Valid UpdateCartItemDto updateCartItemDto) {
        CartItem item = cartService.updateItem(updateCartItemDto);
        Cart cart = cartService.getCart();

        return CartMapper.INSTANCE.convert(item, cart);
    }

    @DeleteMapping("item/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long productId) {
        cartService.removeItem(productId);
    }
}

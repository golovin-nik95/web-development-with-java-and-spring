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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartDto getCart() {
        Cart cart = cartService.getCart();
        return CartMapper.INSTANCE.convert(cart);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemDto addItem(@RequestBody @Valid AddCartItemDto putCartItemDto) {
        CartItem item = cartService.addItem(putCartItemDto.getProductId(), putCartItemDto.getQuantity());
        Cart cart = cartService.getCart();
        return CartMapper.INSTANCE.convert(item, cart);
    }

    @PutMapping
    public CartItemDto updateItem(@RequestBody @Valid UpdateCartItemDto putCartItemDto) {
        CartItem item = cartService.updateItem(putCartItemDto.getProductId(), putCartItemDto.getQuantity());
        Cart cart = cartService.getCart();
        return CartMapper.INSTANCE.convert(item, cart);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@RequestParam Long productId) {
        cartService.removeItem(productId);
    }
}

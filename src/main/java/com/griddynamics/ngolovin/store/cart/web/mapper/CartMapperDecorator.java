package com.griddynamics.ngolovin.store.cart.web.mapper;

import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.web.dto.CartDto;
import com.griddynamics.ngolovin.store.cart.web.dto.CartItemDto;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class CartMapperDecorator implements CartMapper {

    private final CartMapper delegate;

    @Override
    public CartDto convert(Cart source) {
        CartDto target = delegate.convert(source);

        List<CartItemDto> items = source.getItems().values().stream()
                .map((item) -> convert(item, source))
                .collect(Collectors.toList());
        target.setItems(items);

        BigDecimal total = target.getItems().stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        target.setTotal(total);

        return target;
    }

    @Override
    public CartItemDto convert(CartItem source, Cart cart) {
        CartItemDto target = delegate.convert(source, cart);

        Collection<CartItem> items = cart.getItems().values();
        int ordinal = new ArrayList<>(items).indexOf(source) + 1;
        target.setOrdinal(ordinal);

        BigDecimal price = source.getProduct().getPrice();
        BigDecimal quantity = BigDecimal.valueOf(source.getQuantity());
        BigDecimal subtotal = price.multiply(quantity);
        target.setSubtotal(subtotal);

        return target;
    }
}

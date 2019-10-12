package com.griddynamics.ngolovin.store.cart.web.mapper;

import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.web.dto.CartDto;
import com.griddynamics.ngolovin.store.cart.web.dto.CartItemDto;
import com.griddynamics.ngolovin.store.order.domain.OrderEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderItemEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class CartMapperDecorator implements CartMapper {

    private final CartMapper delegate;

    @Override
    public CartDto convert(Cart source) {
        CartDto target = delegate.convert(source);

        List<CartItemDto> cartItems = source.getItems().values().stream()
                .map((cartItem) -> convert(cartItem, source))
                .collect(Collectors.toList());
        target.setItems(cartItems);

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

        Collection<CartItem> cartItems = cart.getItems().values();
        int ordinal = new ArrayList<>(cartItems).indexOf(source) + 1;
        target.setOrdinal(ordinal);

        target.setSubtotal(getSubtotal(source));

        return target;
    }

    @Override
    public OrderEntity convert(Cart source, UserEntity user) {
        OrderEntity target = delegate.convert(source, user);

        AtomicInteger ordinal = new AtomicInteger(1);
        List<OrderItemEntity> orderItems = source.getItems().values().stream()
                .map((cartItem) -> convert(cartItem, ordinal.getAndIncrement()))
                .collect(Collectors.toList());
        target.setItems(orderItems);

        BigDecimal total = target.getItems().stream()
                .map(OrderItemEntity::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        target.setTotal(total);

        target.setDate(LocalDate.now());
        target.setUser(user);
        target.setStatus(OrderStatus.PENDING);

        return target;
    }

    @Override
    public OrderItemEntity convert(CartItem source, int ordinal) {
        OrderItemEntity target = delegate.convert(source, ordinal);

        target.setOrdinal(ordinal);
        target.setProduct(source.getProduct());
        target.setSubtotal(getSubtotal(source));

        return target;
    }

    private BigDecimal getSubtotal(CartItem source) {
        BigDecimal price = source.getProduct().getPrice();
        BigDecimal quantity = BigDecimal.valueOf(source.getQuantity());

        return price.multiply(quantity);
    }
}

package com.griddynamics.ngolovin.store.cart.web.mapper;

import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.web.dto.CartDto;
import com.griddynamics.ngolovin.store.cart.web.dto.CartItemDto;
import com.griddynamics.ngolovin.store.order.domain.OrderEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderItemEntity;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
@DecoratedWith(CartMapperDecorator.class)
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mapping(target = "items", ignore = true)
    CartDto convert(Cart source);

    @Mapping(target = "productTitle", source = "product.title")
    CartItemDto convert(CartItem source, @Context Cart cart);

    @Mapping(target = "items", ignore = true)
    OrderEntity convert(Cart source, @Context UserEntity user);

    @Mapping(target = "product", ignore = true)
    OrderItemEntity convert(CartItem source, @Context int ordinal);
}

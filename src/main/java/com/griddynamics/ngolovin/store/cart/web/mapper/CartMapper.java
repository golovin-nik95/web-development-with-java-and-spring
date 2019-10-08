package com.griddynamics.ngolovin.store.cart.web.mapper;

import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.web.dto.CartDto;
import com.griddynamics.ngolovin.store.cart.web.dto.CartItemDto;
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
@SuppressWarnings("unused")
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mapping(target = "items", source = "items", ignore = true)
    CartDto convert(Cart source);

    @Mapping(target = "productTitle", source = "product.title")
    CartItemDto convert(CartItem source, @Context Cart cart);
}

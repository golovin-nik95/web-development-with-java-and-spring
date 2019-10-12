package com.griddynamics.ngolovin.store.order.web.mapper;

import com.griddynamics.ngolovin.store.order.domain.OrderEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderItemEntity;
import com.griddynamics.ngolovin.store.order.web.dto.OrderDto;
import com.griddynamics.ngolovin.store.order.web.dto.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDto convert(OrderEntity source);

    List<OrderDto> convert(List<OrderEntity> source);

    @Mapping(target = "productTitle", source = "product.title")
    OrderItemDto convert(OrderItemEntity source);
}

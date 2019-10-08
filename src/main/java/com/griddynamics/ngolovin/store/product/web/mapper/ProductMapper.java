package com.griddynamics.ngolovin.store.product.web.mapper;

import com.griddynamics.ngolovin.store.product.domain.Product;
import com.griddynamics.ngolovin.store.product.web.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
@SuppressWarnings("unused")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDto convert(Product source);

    List<ProductDto> convert(List<Product> source);
}

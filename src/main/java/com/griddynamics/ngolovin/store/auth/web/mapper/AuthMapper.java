package com.griddynamics.ngolovin.store.auth.web.mapper;

import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.auth.web.dto.RegisterUserDto;
import com.griddynamics.ngolovin.store.auth.web.dto.TokenDto;
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
@DecoratedWith(AuthMapperDecorator.class)
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    UserEntity convert(RegisterUserDto source, @Context String encryptedPassword);

    @Mapping(target = "token", source = "source")
    @Mapping(target = "type", constant = "Bearer")
    TokenDto convert(String source);
}

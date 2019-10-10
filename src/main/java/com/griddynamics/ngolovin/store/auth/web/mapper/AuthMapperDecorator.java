package com.griddynamics.ngolovin.store.auth.web.mapper;

import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.auth.web.dto.RegisterUserDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AuthMapperDecorator implements AuthMapper {

    private final AuthMapper delegate;

    @Override
    public UserEntity convert(RegisterUserDto source, String encryptedPassword) {
        UserEntity target = delegate.convert(source, encryptedPassword);

        target.setEncryptedPassword(encryptedPassword);

        return target;
    }
}

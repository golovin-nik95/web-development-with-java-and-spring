package com.griddynamics.ngolovin.store.auth.web.controller;

import com.griddynamics.ngolovin.store.auth.service.AuthService;
import com.griddynamics.ngolovin.store.auth.web.dto.LoginUserDto;
import com.griddynamics.ngolovin.store.auth.web.dto.RegisterUserDto;
import com.griddynamics.ngolovin.store.auth.web.dto.TokenDto;
import com.griddynamics.ngolovin.store.auth.web.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@RequestBody @Valid RegisterUserDto registerUserDto) {
        authService.registerUser(registerUserDto);
    }

    @PostMapping("login")
    public TokenDto loginUser(@RequestBody @Valid LoginUserDto loginUserDto) {
        String token = authService.loginUser(loginUserDto);

        return AuthMapper.INSTANCE.convert(token);
    }
}

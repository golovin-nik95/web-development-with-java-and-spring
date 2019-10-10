package com.griddynamics.ngolovin.store.auth.service;

import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.auth.exception.UserAlreadyExistsException;
import com.griddynamics.ngolovin.store.auth.jwt.JwtTokenProvider;
import com.griddynamics.ngolovin.store.auth.web.dto.LoginUserDto;
import com.griddynamics.ngolovin.store.auth.web.dto.RegisterUserDto;
import com.griddynamics.ngolovin.store.auth.web.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public void registerUser(RegisterUserDto registerUserDto) {
        userService.getUserByEmail(registerUserDto.getEmail())
                .ifPresent(x -> { throw new UserAlreadyExistsException(registerUserDto.getEmail()); });

        String encryptedPassword = passwordEncoder.encode(registerUserDto.getPassword());
        UserEntity user = AuthMapper.INSTANCE.convert(registerUserDto, encryptedPassword);
        userService.addUser(user);
    }

    public String loginUser(LoginUserDto loginUserDto) {
        Authentication authentication = getAuthentication(loginUserDto.getEmail(), loginUserDto.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    private Authentication getAuthentication(String email, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManager.authenticate(authentication);
    }
}

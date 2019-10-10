package com.griddynamics.ngolovin.store.auth.jwt;

import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.getUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with email [" + username + "] doesn't exist"));

        return convert(user);
    }

    UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        UserEntity user = userService.getUserById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id [" + id + "] doesn't exist"));

        return convert(user);
    }

    private UserDetails convert(UserEntity user) {
        return new User(user.getEmail(), user.getEncryptedPassword(), new ArrayList<>());
    }
}

package com.griddynamics.ngolovin.store.auth.service;

import com.griddynamics.ngolovin.store.auth.dao.UserRepository;
import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    void saveUser(UserEntity user) {
        userRepository.save(user);
    }
}

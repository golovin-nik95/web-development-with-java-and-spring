package com.griddynamics.ngolovin.store.auth.service;

import com.griddynamics.ngolovin.store.StoreTestUtils;
import com.griddynamics.ngolovin.store.auth.dao.UserRepository;
import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private UserEntity user;

    @Before
    public void setUp() {
        user = StoreTestUtils.createUser();
    }

    @Test
    public void getUserByIdTest() {
        Long userId = user.getId();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Optional<UserEntity> actual = userService.getUserById(userId);

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(user);
    }

    @Test
    public void getUserById_UserNotFoundTest() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Optional<UserEntity> actual = userService.getUserById(userId);

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        assertThat(actual).isEmpty();
    }

    @Test
    public void getUserByEmailTest() {
        String userEmail = user.getEmail();
        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        Optional<UserEntity> actual = userService.getUserByEmail(userEmail);

        verify(userRepository).findByEmail(userEmail);
        verifyNoMoreInteractions(userRepository);
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(user);
    }

    @Test
    public void getUserByEmail_UserNotFoundTest() {
        String email = "test@mail.ru";
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        Optional<UserEntity> actual = userService.getUserByEmail(email);

        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
        assertThat(actual).isEmpty();
    }

    @Test
    public void saveUserTest() {
        userService.saveUser(user);

        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }
}
package com.griddynamics.ngolovin.store.auth.service;

import com.griddynamics.ngolovin.store.StoreTestUtils;
import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.auth.exception.UserAlreadyExistsException;
import com.griddynamics.ngolovin.store.auth.jwt.JwtTokenProvider;
import com.griddynamics.ngolovin.store.auth.web.dto.LoginUserDto;
import com.griddynamics.ngolovin.store.auth.web.dto.RegisterUserDto;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Captor
    public ArgumentCaptor<Authentication> authenticationCaptor;

    private UserEntity user;

    @Before
    public void setUp() {
        user = StoreTestUtils.createUser();
    }

    @Test
    public void registerUserTest() {
        user.setId(null);
        String userEmail = user.getEmail();
        String userPassword = "123456";
        RegisterUserDto registerUserDto = new RegisterUserDto(user.getName(), userEmail, userPassword);
        when(userService.getUserByEmail(userEmail))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(userPassword))
                .thenReturn(user.getEncryptedPassword());

        authService.registerUser(registerUserDto);

        verify(userService).getUserByEmail(userEmail);
        verify(userService).saveUser(eq(user));
        verifyNoMoreInteractions(userService);
        verify(passwordEncoder).encode(userPassword);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(jwtTokenProvider);
        verifyNoMoreInteractions(authenticationManager);
    }

    @Test
    public void registerUser_UserAlreadyExistsTest() {
        String userEmail = user.getEmail();
        RegisterUserDto registerUserDto = new RegisterUserDto(user.getName(), userEmail, "123456");
        when(userService.getUserByEmail(userEmail))
                .thenReturn(Optional.of(user));
        thrown.expect(UserAlreadyExistsException.class);

        authService.registerUser(registerUserDto);

        verify(userService).getUserByEmail(userEmail);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(jwtTokenProvider);
        verifyNoMoreInteractions(authenticationManager);
    }

    @Test
    public void loginUserTest() {
        LoginUserDto loginUserDto = new LoginUserDto(user.getEmail(), "123456");
        String jwtToken = "jwtToken";
        when(jwtTokenProvider.generateToken(any(Authentication.class)))
                .thenReturn(jwtToken);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .then(i -> i.getArgument(0));

        String actual = authService.loginUser(loginUserDto);

        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(passwordEncoder);
        verify(jwtTokenProvider).generateToken(authenticationCaptor.capture());
        verifyNoMoreInteractions(jwtTokenProvider);
        verify(authenticationManager).authenticate(authenticationCaptor.getValue());
        verifyNoMoreInteractions(authenticationManager);
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(jwtToken);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authenticationCaptor.getValue());
    }

    @Test
    public void getCurrentLoggedInUserEmailTest() {
        String userEmail = user.getEmail();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, user.getEncryptedPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String actual = authService.getCurrentLoggedInUserEmail();

        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(jwtTokenProvider);
        verifyNoMoreInteractions(authenticationManager);
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(userEmail);
    }

    @Test
    public void getCurrentLoggedInUserEmail_AuthenticationIsNullTest() {
        String actual = authService.getCurrentLoggedInUserEmail();

        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(jwtTokenProvider);
        verifyNoMoreInteractions(authenticationManager);
        assertThat(actual).isNull();
    }
}
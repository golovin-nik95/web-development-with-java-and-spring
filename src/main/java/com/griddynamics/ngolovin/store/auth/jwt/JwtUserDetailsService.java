package com.griddynamics.ngolovin.store.auth.jwt;

import com.griddynamics.ngolovin.store.auth.bruteforce.LoginAttemptService;
import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.auth.exception.UserIsBlockedException;
import com.griddynamics.ngolovin.store.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private static final String USER_DETAILS_SESSION_ATTRIBUTE = "userDetails";

    private final UserService userService;
    private final HttpSession session;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        checkBruteForce();

        return getUserDetailsFromSession()
                .orElseGet(() -> {
                    UserDetails userDetails = userService.getUserByEmail(username)
                            .map(this::convert)
                            .orElseThrow(() ->
                                    new UsernameNotFoundException("User with email [" + username + "] doesn't exist"));
                    session.setAttribute(USER_DETAILS_SESSION_ATTRIBUTE, userDetails);

                    return userDetails;
                });
    }

    UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        checkBruteForce();

        return getUserDetailsFromSession()
                .orElseGet(() -> {
                    UserDetails userDetails = userService.getUserById(id)
                            .map(this::convert)
                            .orElseThrow(() ->
                                    new UsernameNotFoundException("User with id [" + id + "] doesn't exist"));
                    session.setAttribute(USER_DETAILS_SESSION_ATTRIBUTE, userDetails);

                    return userDetails;
                });
    }

    private void checkBruteForce() {
        if (loginAttemptService.isBlocked()) {
            throw new UserIsBlockedException();
        }
    }

    private Optional<UserDetails> getUserDetailsFromSession() {
        try {
            UserDetails userDetails = (UserDetails) session.getAttribute(USER_DETAILS_SESSION_ATTRIBUTE);

            return Optional.ofNullable(userDetails);
        } catch (Exception e) {
            log.error("Failed to get user details from session", e);
        }

        return Optional.empty();
    }

    private UserDetails convert(UserEntity user) {
        return new User(user.getEmail(), user.getEncryptedPassword(), new ArrayList<>());
    }
}

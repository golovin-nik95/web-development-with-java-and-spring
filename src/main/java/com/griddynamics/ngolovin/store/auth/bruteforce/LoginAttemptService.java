package com.griddynamics.ngolovin.store.auth.bruteforce;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginAttemptService {

    private final HttpServletRequest request;
    private final LoginAttemptCacheProperties loginAttemptCacheProperties;

    private LoadingCache<String, Integer> loginAttemptCache;

    @PostConstruct
    private void setUp() {
        loginAttemptCache = Caffeine.newBuilder()
                .expireAfterWrite(loginAttemptCacheProperties.getTimeToLive(), TimeUnit.MILLISECONDS)
                .build(ipAddress -> 0);
    }

    void loginSucceeded() {
        String ipAddress = getIpAddress();

        loginAttemptCache.invalidate(ipAddress);
    }

    @SuppressWarnings("ConstantConditions")
    void loginFailed() {
        String ipAddress = getIpAddress();

        int loginAttempts;
        try {
            loginAttempts = loginAttemptCache.get(ipAddress);
        } catch (Exception e) {
            log.warn("Failed to get or compute value with ipAddress=[{}] from loginAttemptsCache", ipAddress, e);
            loginAttempts = 0;
        }

        loginAttemptCache.put(ipAddress, ++loginAttempts);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean isBlocked() {
        String ipAddress = getIpAddress();

        try {
            return loginAttemptCache.get(ipAddress) >= loginAttemptCacheProperties.getMaxLoginAttempts();
        } catch (Exception e) {
            log.warn("Failed to get or compute value with ipAddress=[{}] from loginAttemptsCache", ipAddress, e);
        }

        return false;
    }

    private String getIpAddress() {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null){
            return request.getRemoteAddr();
        }

        return xForwardedForHeader.split(",")[0];
    }
}

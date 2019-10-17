package com.griddynamics.ngolovin.store.auth.bruteforce;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "store.brute-force.login-attempt-cache")
@Data
public class LoginAttemptCacheProperties {

    private Integer maxLoginAttempts;
    private Long timeToLive;
}

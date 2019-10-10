package com.griddynamics.ngolovin.store.auth.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "store.jwt.token")
@Data
public class JwtTokenProperties {

    private String secretKey;
    private Long timeToLive;
}

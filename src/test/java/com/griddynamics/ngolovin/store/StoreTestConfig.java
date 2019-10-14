package com.griddynamics.ngolovin.store;

import com.griddynamics.ngolovin.store.cart.domain.Cart;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class StoreTestConfig {

    public static final String USER_NAME = "Ivan Ivanov";
    public static final String USER_EMAIL = "test@griddynamics.com";
    public static final String USER_PASSWORD = "123456";

    @Bean
    @Primary
    public Cart singletonCart() {
        return new Cart();
    }
}

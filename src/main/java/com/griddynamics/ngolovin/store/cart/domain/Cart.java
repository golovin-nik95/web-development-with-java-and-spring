package com.griddynamics.ngolovin.store.cart.domain;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@SessionScope
@Data
public class Cart {

    private Map<Long, CartItem> items = new LinkedHashMap<>();
}

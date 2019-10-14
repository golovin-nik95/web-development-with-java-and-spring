package com.griddynamics.ngolovin.store.cart.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.ngolovin.store.StoreTestConfig;
import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.web.dto.AddCartItemDto;
import com.griddynamics.ngolovin.store.cart.web.dto.CartDto;
import com.griddynamics.ngolovin.store.cart.web.dto.CartItemDto;
import com.griddynamics.ngolovin.store.cart.web.dto.UpdateCartItemDto;
import com.griddynamics.ngolovin.store.product.dao.ProductRepository;
import com.griddynamics.ngolovin.store.product.domain.ProductEntity;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {StoreTestConfig.class})
@Transactional
@AutoConfigureMockMvc
@WithMockUser(username = StoreTestConfig.USER_EMAIL, password = StoreTestConfig.USER_PASSWORD)
@RunWith(SpringRunner.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Cart cart;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setUp() {
        fillCart();
    }

    @Test
    public void getCartTest() throws Exception {
        String responseBody = mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();
        CartDto cartDto = objectMapper.readValue(responseBody, CartDto.class);
        Assertions.assertThat(cartDto.getItems()).hasSize(3);
        Assertions.assertThat(cartDto.getTotal()).isEqualByComparingTo("93.40");

        CartItemDto cartItemDto = cartDto.getItems().get(0);
        Assertions.assertThat(cartItemDto.getOrdinal()).isEqualTo(1);
        Assertions.assertThat(cartItemDto.getProductTitle()).isEqualTo("Pen");
        Assertions.assertThat(cartItemDto.getQuantity()).isEqualTo(7L);
        Assertions.assertThat(cartItemDto.getSubtotal()).isEqualByComparingTo("53.20");
    }

    @Test
    public void addCartItemTest() throws Exception {
        Map<Long, CartItem> cartItems = cart.getItems();
        ProductEntity product = productRepository.findAll().stream()
                .sorted(Comparator.comparingLong(ProductEntity::getId))
                .skip(cartItems.size())
                .findAny()
                .orElseThrow(AssertionError::new);
        Long productId = product.getId();
        long quantity = product.getAvailable() / 2;
        BigDecimal subtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));
        AddCartItemDto addCartItemDto = new AddCartItemDto(productId, quantity);
        String requestBody = objectMapper.writeValueAsString(addCartItemDto);

        String responseBody = mockMvc.perform(post("/api/cart/item")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(authenticated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();
        CartItemDto cartItemDto = objectMapper.readValue(responseBody, CartItemDto.class);
        Assertions.assertThat(cartItemDto.getOrdinal()).isEqualTo(4L);
        Assertions.assertThat(cartItemDto.getProductTitle()).isEqualTo(product.getTitle());
        Assertions.assertThat(cartItemDto.getQuantity()).isEqualTo(quantity);
        Assertions.assertThat(cartItemDto.getSubtotal()).isEqualByComparingTo(subtotal);

        Assertions.assertThat(cartItems.get(productId)).isNotNull();
    }

    @Test
    public void updateCartItemTest() throws Exception {
        Map<Long, CartItem> cartItems = cart.getItems();
        CartItem cartItem = cartItems.values().iterator().next();
        ProductEntity product = cartItem.getProduct();
        Long productId = product.getId();
        long quantity = cartItem.getQuantity() + 1;
        BigDecimal subtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));
        UpdateCartItemDto updateCartItemDto = new UpdateCartItemDto(productId, quantity);
        String requestBody = objectMapper.writeValueAsString(updateCartItemDto);

        String responseBody = mockMvc.perform(put("/api/cart/item")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();
        CartItemDto cartItemDto = objectMapper.readValue(responseBody, CartItemDto.class);
        Assertions.assertThat(cartItemDto.getOrdinal()).isEqualTo(1L);
        Assertions.assertThat(cartItemDto.getProductTitle()).isEqualTo(product.getTitle());
        Assertions.assertThat(cartItemDto.getQuantity()).isEqualTo(quantity);
        Assertions.assertThat(cartItemDto.getSubtotal()).isEqualByComparingTo(subtotal);

        Assertions.assertThat(cartItems.get(productId).getQuantity()).isEqualTo(quantity);
    }

    @Test
    public void removeCartItemTest() throws Exception {
        Map<Long, CartItem> cartItems = cart.getItems();
        Long productId = cartItems.keySet().iterator().next();

        String responseBody = mockMvc.perform(delete("/api/cart/item/{productId}", productId))
                .andExpect(status().isNoContent())
                .andExpect(authenticated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Assertions.assertThat(responseBody).isEmpty();

        Assertions.assertThat(cartItems).hasSize(2);
        Assertions.assertThat(cartItems.get(productId)).isNull();
    }

    private void fillCart() {
        Map<Long, CartItem> cartItems = productRepository.findAll().stream()
                .sorted(Comparator.comparingLong(ProductEntity::getId))
                .limit(3)
                .collect(Collectors.toMap(ProductEntity::getId,
                        product -> new CartItem(product, product.getAvailable() / 2)));
        cart.setItems(cartItems);
    }
}
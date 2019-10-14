package com.griddynamics.ngolovin.store.order.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.ngolovin.store.StoreTestConfig;
import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.order.dao.OrderRepository;
import com.griddynamics.ngolovin.store.order.domain.OrderEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderItemEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderStatus;
import com.griddynamics.ngolovin.store.order.web.dto.OrderDto;
import com.griddynamics.ngolovin.store.order.web.dto.OrderItemDto;
import com.griddynamics.ngolovin.store.product.dao.ProductRepository;
import com.griddynamics.ngolovin.store.product.domain.ProductEntity;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.griddynamics.ngolovin.store.StoreTestConfig.USER_EMAIL;
import static com.griddynamics.ngolovin.store.StoreTestConfig.USER_PASSWORD;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {StoreTestConfig.class})
@Transactional
@AutoConfigureMockMvc
@WithMockUser(username = USER_EMAIL, password = USER_PASSWORD)
@RunWith(SpringRunner.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Cart cart;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Sql(scripts = {"classpath:/insert-user.sql", "classpath:/insert-orders.sql"})
    public void getAllOrdersTest() throws Exception {
        String responseBody = mockMvc.perform(get("/api/order"))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OrderDto> orderDtos = objectMapper.readValue(responseBody, new TypeReference<List<OrderDto>>() {});
        orderDtos.sort(Comparator.comparingLong(OrderDto::getId));
        Assertions.assertThat(orderDtos).hasSize(3);

        OrderDto orderDto = orderDtos.get(0);
        Assertions.assertThat(orderDto.getId()).isEqualTo(1L);
        Assertions.assertThat(orderDto.getDate()).isEqualTo(LocalDate.of(2019, 10, 14));
        Assertions.assertThat(orderDto.getTotal()).isEqualByComparingTo("93.40");
        Assertions.assertThat(orderDto.getStatus()).isEqualTo(OrderStatus.PENDING);
        Assertions.assertThat(orderDto.getItems()).hasSize(3);

        OrderItemDto orderItemDto = orderDto.getItems().get(0);
        Assertions.assertThat(orderItemDto.getOrdinal()).isEqualTo(1);
        Assertions.assertThat(orderItemDto.getProductTitle()).isEqualTo("Pen");
        Assertions.assertThat(orderItemDto.getQuantity()).isEqualTo(7);
        Assertions.assertThat(orderItemDto.getSubtotal()).isEqualTo("53.20");
    }

    @Test
    @Sql(scripts = "classpath:/insert-user.sql")
    public void checkoutOrderTest() throws Exception {
        fillCart();
        Map<Long, CartItem> cartItems = cart.getItems();
        Map<Long, Long> productsAvailabilityBefore = cartItems.values().stream()
                .map(CartItem::getProduct)
                .collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getAvailable));

        String responseBody = mockMvc.perform(post("/api/order"))
                .andExpect(status().isCreated())
                .andExpect(authenticated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();
        OrderDto orderDto = objectMapper.readValue(responseBody, OrderDto.class);

        Assertions.assertThat(orderDto.getId()).isNotNull();
        Assertions.assertThat(orderDto.getDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(orderDto.getTotal()).isEqualByComparingTo("93.40");
        Assertions.assertThat(orderDto.getStatus()).isEqualTo(OrderStatus.PENDING);
        Assertions.assertThat(orderDto.getItems()).hasSize(3);

        OrderItemDto orderItemDto = orderDto.getItems().get(0);
        Assertions.assertThat(orderItemDto.getOrdinal()).isEqualTo(1);
        Assertions.assertThat(orderItemDto.getProductTitle()).isEqualTo("Pen");
        Assertions.assertThat(orderItemDto.getQuantity()).isEqualTo(7L);
        Assertions.assertThat(orderItemDto.getSubtotal()).isEqualByComparingTo("53.20");

        Assertions.assertThat(cartItems).isEmpty();

        productRepository.findAllById(productsAvailabilityBefore.keySet()).stream()
                .collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getAvailable))
                .forEach((productId, availableAfter) -> {
                    Long availableBefore = productsAvailabilityBefore.get(productId);
                    Assertions.assertThat(availableAfter).isEqualTo(availableBefore - availableBefore / 2);
                });
    }

    @Test
    @Sql(scripts = {"classpath:/insert-user.sql", "classpath:/insert-orders.sql"})
    public void cancelOrderTest() throws Exception {
        OrderEntity orderBefore = orderRepository.findAll().stream()
                .findAny()
                .orElseThrow(AssertionError::new);
        Long orderId = orderBefore.getId();
        Map<Long, Long> productsAvailabilityBefore = orderBefore.getItems().stream()
                .map(OrderItemEntity::getProduct)
                .collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getAvailable));

        String responseBody = mockMvc.perform(delete("/api/order/{id}", orderId))
                .andExpect(status().isNoContent())
                .andExpect(authenticated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Assertions.assertThat(responseBody).isEmpty();

        OrderEntity orderAfter = orderRepository.findById(orderId)
                .orElseThrow(AssertionError::new);
        Assertions.assertThat(orderAfter.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        productRepository.findAllById(productsAvailabilityBefore.keySet()).stream()
                .collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getAvailable))
                .forEach((productId, availableAfter) -> {
                    Long availableBefore = productsAvailabilityBefore.get(productId);
                    Assertions.assertThat(availableAfter).isEqualTo(availableBefore * 3 / 2);
                });
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
package com.griddynamics.ngolovin.store;

import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.order.domain.OrderEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderItemEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderStatus;
import com.griddynamics.ngolovin.store.product.domain.ProductEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class StoreTestUtils {

    public static List<ProductEntity> createProducts() {
        return LongStream.range(1, 6)
                .mapToObj(i -> ProductEntity.builder()
                        .id(i)
                        .title("product#" + i)
                        .available(5L)
                        .price(BigDecimal.ONE)
                        .build())
                .collect(Collectors.toList());
    }

    public static Map<Long, CartItem> createCartItems() {
        return createProducts().stream()
                .limit(3)
                .collect(Collectors.toMap(ProductEntity::getId,
                        product -> new CartItem(product, product.getAvailable() / 2)));
    }

    public static UserEntity createUser() {
        return UserEntity.builder()
                .id(1L)
                .email("test@griddynamics.com")
                .encryptedPassword("encrypted123456")
                .name("Ivan Ivanov")
                .build();
    }

    public static List<OrderEntity> createOrders() {
        List<ProductEntity> products = createProducts();
        UserEntity user = createUser();
        return IntStream.range(0, 3)
                .mapToObj(i -> {
                    List<OrderItemEntity> orderItems = IntStream.range(0, 3)
                            .mapToObj(j -> {
                                ProductEntity product = products.get(i + j);
                                return OrderItemEntity.builder()
                                        .id(i * 3L + j)
                                        .ordinal(j)
                                        .product(product)
                                        .quantity(product.getAvailable() / 2)
                                        .subtotal(BigDecimal.ONE)
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return OrderEntity.builder()
                            .id(i + 1L)
                            .date(LocalDate.now())
                            .total(BigDecimal.TEN)
                            .status(OrderStatus.PENDING)
                            .user(user)
                            .items(orderItems)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

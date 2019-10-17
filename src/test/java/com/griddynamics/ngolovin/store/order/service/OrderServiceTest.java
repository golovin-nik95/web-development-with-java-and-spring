package com.griddynamics.ngolovin.store.order.service;

import com.griddynamics.ngolovin.store.StoreTestUtils;
import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.auth.exception.UserNotFoundException;
import com.griddynamics.ngolovin.store.auth.service.AuthService;
import com.griddynamics.ngolovin.store.auth.service.UserService;
import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.exception.InsufficientProductQuantityException;
import com.griddynamics.ngolovin.store.cart.exception.ProductNotFoundException;
import com.griddynamics.ngolovin.store.order.dao.OrderRepository;
import com.griddynamics.ngolovin.store.order.domain.OrderEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderItemEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderStatus;
import com.griddynamics.ngolovin.store.order.exception.EmptyCartException;
import com.griddynamics.ngolovin.store.order.exception.OrderNotFoundException;
import com.griddynamics.ngolovin.store.product.domain.ProductEntity;
import com.griddynamics.ngolovin.store.product.service.ProductService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private Cart cart;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderRepository orderRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Map<Long, CartItem> cartItems;
    private UserEntity user;
    private List<OrderEntity> orders;

    @Before
    public void setUp() {
        cartItems = StoreTestUtils.createCartItems();
        user = StoreTestUtils.createUser();
        orders = StoreTestUtils.createOrders();
    }

    @Test
    public void getAllOrdersTest() {
        String email = user.getEmail();
        when(authService.getCurrentLoggedInUserEmail())
                .thenReturn(email);
        when(orderRepository.findByUser_Email(email))
                .thenReturn(orders);

        List<OrderEntity> actual = orderService.getAllOrders();

        verifyNoMoreInteractions(cart);
        verify(authService).getCurrentLoggedInUserEmail();
        verifyNoMoreInteractions(authService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(productService);
        verify(orderRepository).findByUser_Email(email);
        verifyNoMoreInteractions(orderRepository);
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(orders);
    }

    @Test
    public void checkoutOrderTest() {
        Map<Long, Long> productsAvailabilityBefore = cartItems.values().stream()
                .map(CartItem::getProduct)
                .collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getAvailable));
        String userEmail = user.getEmail();
        when(cart.getItems())
                .thenReturn(cartItems)
                .thenReturn(cartItems);
        when(authService.getCurrentLoggedInUserEmail())
                .thenReturn(userEmail);
        when(userService.getUserByEmail(userEmail))
                .thenReturn(Optional.of(user));
        //noinspection SuspiciousMethodCalls
        when(productService.getProductById(anyLong()))
                .then(i -> Optional.of(cartItems.get(i.getArgument(0)).getProduct()));
        when(orderRepository.save(any(OrderEntity.class)))
                .then(i -> i.getArgument(0));

        OrderEntity actual = orderService.checkoutOrder();

        //noinspection ResultOfMethodCallIgnored
        verify(cart, times(2)).getItems();
        verifyNoMoreInteractions(cart);
        verify(authService).getCurrentLoggedInUserEmail();
        verifyNoMoreInteractions(authService);
        verify(userService).getUserByEmail(userEmail);
        verifyNoMoreInteractions(userService);
        verify(productService, times(3)).getProductById(anyLong());
        verifyNoMoreInteractions(productService);
        verify(orderRepository).save(actual);
        verifyNoMoreInteractions(orderRepository);
        assertThat(actual).isNotNull();
        assertThat(actual.getDate()).isEqualTo(LocalDate.now());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(actual.getUser()).isEqualTo(user);
        List<OrderItemEntity> orderItems = actual.getItems();
        assertThat(orderItems).hasSize(productsAvailabilityBefore.size());
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItemEntity orderItem = orderItems.get(i);
            ProductEntity product = orderItem.getProduct();
            Long availableBefore = productsAvailabilityBefore.get(product.getId());
            Long quantity = orderItem.getQuantity();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(subtotal);
            assertThat(orderItem.getOrdinal()).isEqualTo(i + 1);
            assertThat(orderItem.getSubtotal()).isEqualByComparingTo(subtotal);
            assertThat(quantity).isEqualTo(availableBefore / 2);
            assertThat(product.getAvailable()).isEqualTo(availableBefore - quantity);
        }
        assertThat(actual.getTotal()).isEqualByComparingTo(total);
        assertThat(cartItems).isEmpty();
    }

    @Test
    public void checkoutOrder_EmptyCartTest() {
        when(cart.getItems())
                .thenReturn(Collections.emptyMap());
        thrown.expect(EmptyCartException.class);

        orderService.checkoutOrder();

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(authService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void checkoutOrder_ProductNotFoundTest() {
        Long productId = cartItems.values().iterator().next().getProduct().getId();
        when(cart.getItems())
                .thenReturn(cartItems);
        when(productService.getProductById(productId))
                .thenReturn(Optional.empty());
        thrown.expect(ProductNotFoundException.class);

        orderService.checkoutOrder();

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(authService);
        verifyNoMoreInteractions(userService);
        verify(productService).getProductById(productId);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void checkoutOrder_InsufficientProductQuantityTest() {
        CartItem cartItem = cartItems.values().iterator().next();
        ProductEntity product = cartItem.getProduct();
        Long productId = product.getId();
        product.setAvailable(cartItem.getQuantity() - 1);
        when(cart.getItems())
                .thenReturn(cartItems);
        when(productService.getProductById(productId))
                .thenReturn(Optional.of(product));
        thrown.expect(InsufficientProductQuantityException.class);

        orderService.checkoutOrder();

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(authService);
        verifyNoMoreInteractions(userService);
        verify(productService).getProductById(productId);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void checkoutOrder_UserNotFoundTest() {
        String userEmail = user.getEmail();
        when(cart.getItems())
                .thenReturn(cartItems);
        when(authService.getCurrentLoggedInUserEmail())
                .thenReturn(userEmail);
        when(userService.getUserByEmail(userEmail))
                .thenReturn(Optional.empty());
        //noinspection SuspiciousMethodCalls
        when(productService.getProductById(anyLong()))
                .then(i -> Optional.of(cartItems.get(i.getArgument(0)).getProduct()));
        thrown.expect(UserNotFoundException.class);

        orderService.checkoutOrder();

        //noinspection ResultOfMethodCallIgnored
        verify(cart).getItems();
        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(authService);
        verifyNoMoreInteractions(userService);
        verify(productService, times(5)).getProductById(anyLong());
        verifyNoMoreInteractions(productService);
        verify(orderRepository).save(any(OrderEntity.class));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void cancelOrderTest() {
        OrderEntity order = orders.iterator().next();
        Map<Long, Long> productsAvailabilityBefore = order.getItems().stream()
                .map(OrderItemEntity::getProduct)
                .collect(Collectors.toMap(ProductEntity::getId, ProductEntity::getAvailable));
        Long orderId = order.getId();
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        orderService.cancelOrder(orderId);

        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(authService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(productService);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
        verifyNoMoreInteractions(orderRepository);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        order.getItems().forEach(orderItem -> {
            ProductEntity product = orderItem.getProduct();
            Long availableBefore = productsAvailabilityBefore.get(product.getId());
            assertThat(product.getAvailable()).isEqualTo(availableBefore + orderItem.getQuantity());
        });
    }

    @Test
    public void cancelOrder_OrderNotFoundTest() {
        long orderId = 1L;
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());
        thrown.expect(OrderNotFoundException.class);

        orderService.cancelOrder(orderId);

        verifyNoMoreInteractions(cart);
        verifyNoMoreInteractions(authService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(productService);
        verify(orderRepository).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
    }
}
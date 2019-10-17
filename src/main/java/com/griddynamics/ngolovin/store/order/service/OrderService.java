package com.griddynamics.ngolovin.store.order.service;

import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.auth.exception.UserNotFoundException;
import com.griddynamics.ngolovin.store.auth.service.AuthService;
import com.griddynamics.ngolovin.store.auth.service.UserService;
import com.griddynamics.ngolovin.store.cart.domain.Cart;
import com.griddynamics.ngolovin.store.cart.domain.CartItem;
import com.griddynamics.ngolovin.store.cart.exception.InsufficientProductQuantityException;
import com.griddynamics.ngolovin.store.cart.exception.ProductNotFoundException;
import com.griddynamics.ngolovin.store.cart.web.mapper.CartMapper;
import com.griddynamics.ngolovin.store.order.dao.OrderRepository;
import com.griddynamics.ngolovin.store.order.domain.OrderEntity;
import com.griddynamics.ngolovin.store.order.domain.OrderStatus;
import com.griddynamics.ngolovin.store.order.exception.EmptyCartException;
import com.griddynamics.ngolovin.store.order.exception.OrderNotFoundException;
import com.griddynamics.ngolovin.store.product.domain.ProductEntity;
import com.griddynamics.ngolovin.store.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final Cart cart;
    private final AuthService authService;
    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;

    public List<OrderEntity> getAllOrders() {
        String userEmail = authService.getCurrentLoggedInUserEmail();

        return orderRepository.findByUser_Email(userEmail);
    }

    public OrderEntity checkoutOrder() {
        Collection<CartItem> cartItems = cart.getItems().values();
        if (CollectionUtils.isEmpty(cartItems)) {
            throw new EmptyCartException();
        }

        OrderEntity createdOrder = createOrder(cartItems);
        cartItems.clear();

        return createdOrder;
    }

    public void cancelOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        returnBackOrderedProducts(order);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private OrderEntity createOrder(Collection<CartItem> cartItems) {
        updateProductsInCartItems(cartItems);
        OrderEntity order = CartMapper.INSTANCE.convert(cart, getUser());

        return orderRepository.save(order);
    }

    private void updateProductsInCartItems(Collection<CartItem> cartItems) {
        cartItems.forEach(cartItem -> {
            long quantity = cartItem.getQuantity();
            ProductEntity product = getProduct(cartItem.getProduct().getId(), quantity);

            long newAvailable = product.getAvailable() - quantity;
            product.setAvailable(newAvailable);

            cartItem.setProduct(product);
        });
    }

    private ProductEntity getProduct(Long productId, Long quantity) {
        ProductEntity product = productService.getProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (quantity > product.getAvailable()) {
            throw new InsufficientProductQuantityException(productId);
        }

        return product;
    }

    private UserEntity getUser() {
        String userEmail = authService.getCurrentLoggedInUserEmail();
        return userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));
    }

    private void returnBackOrderedProducts(OrderEntity order) {
        order.getItems().forEach(orderItem -> {
            ProductEntity product = orderItem.getProduct();
            Long newAvailable = product.getAvailable() + orderItem.getQuantity();
            product.setAvailable(newAvailable);
        });
    }
}

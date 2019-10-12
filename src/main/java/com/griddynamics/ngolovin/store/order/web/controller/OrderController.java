package com.griddynamics.ngolovin.store.order.web.controller;

import com.griddynamics.ngolovin.store.order.domain.OrderEntity;
import com.griddynamics.ngolovin.store.order.service.OrderService;
import com.griddynamics.ngolovin.store.order.web.dto.OrderDto;
import com.griddynamics.ngolovin.store.order.web.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getAllOrders() {
        List<OrderEntity> orders = orderService.getAllOrders();

        return OrderMapper.INSTANCE.convert(orders);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto checkoutOrder() {
        OrderEntity order = orderService.checkoutOrder();

        return OrderMapper.INSTANCE.convert(order);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }
}
